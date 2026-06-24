package com.techmart.mdb;

import com.techmart.dao.OrderDAO;
import com.techmart.entity.Order;
import com.techmart.entity.Order.OrderStatus;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Message-Driven Bean listening to OrderQueue for automated order processing.
 *
 * MDB Lifecycle:
 * 1. Container creates MDB instance pool on deployment
 * 2. onMessage() invoked for each queue message (container-managed transactions)
 * 3. Instance returned to pool after processing
 * 4. @PreDestroy called on undeployment
 *
 * Why MDB: Decouples order placement from fulfillment. Provides reliable,
 * asynchronous processing with automatic transaction management and load distribution
 * across MDB instances in clustered deployments.
 */
@MessageDriven(name = "OrderMDB", activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/queue/OrderQueue"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "15")
})
public class OrderMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(OrderMDB.class.getName());
    private static final int MAX_PROCESSING_LOG = 200;

    @EJB
    private OrderDAO orderDAO;

    private static final List<String> processingLog = new CopyOnWriteArrayList<>();

    @jakarta.annotation.PostConstruct
    public void init() {
        LOGGER.info("OrderMDB initialized - listening on OrderQueue");
    }

    @jakarta.annotation.PreDestroy
    public void destroy() {
        LOGGER.info("OrderMDB destroyed - " + processingLog.size() + " messages processed");
    }

    @Override
    public void onMessage(Message message) {
        long startTime = System.currentTimeMillis();

        try {
            if (!(message instanceof TextMessage)) {
                LOGGER.warning("Received non-text message, ignoring");
                return;
            }

            TextMessage textMessage = (TextMessage) message;
            String body = textMessage.getText();
            String messageType = textMessage.getStringProperty("messageType");

            LOGGER.info("OrderMDB received message: " + body + " [type=" + messageType + "]");

            // Parse message: orderId|customerEmail|totalAmount
            String[] parts = body.split("\\|");
            if (parts.length < 3) {
                logProcessing("ERROR", "Invalid message format: " + body);
                return;
            }

            Long orderId = Long.parseLong(parts[0]);
            String customerEmail = parts[1];
            String totalAmount = parts[2];

            processOrder(orderId, customerEmail, totalAmount);

            long elapsed = System.currentTimeMillis() - startTime;
            logProcessing("SUCCESS", "Order " + orderId + " processed in " + elapsed + "ms");

        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "JMS error in OrderMDB", e);
            logProcessing("ERROR", "JMS error: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Order processing failed in MDB", e);
            logProcessing("ERROR", "Processing failed: " + e.getMessage());
        }
    }

    private void processOrder(Long orderId, String customerEmail, String totalAmount) {
        Order order = orderDAO.findById(orderId).orElse(null);
        if (order == null) {
            logProcessing("ERROR", "Order not found: " + orderId);
            return;
        }

        // Stock already reserved during order placement — update fulfillment status only
        order.setStatus(OrderStatus.PROCESSING);
        orderDAO.update(order);

        // Simulate fulfillment steps
        order.setStatus(OrderStatus.SHIPPED);
        orderDAO.update(order);

        logProcessing("FULFILLED",
                "Order " + orderId + " shipped to " + customerEmail + " - Total: $" + totalAmount);
    }

    private void logProcessing(String status, String message) {
        String entry = String.format("[%s] %s: %s", status, System.currentTimeMillis(), message);
        processingLog.add(0, entry);
        while (processingLog.size() > MAX_PROCESSING_LOG) {
            processingLog.remove(processingLog.size() - 1);
        }
        LOGGER.info("MDB Log: " + entry);
    }

    public static List<String> getProcessingLog() {
        return new ArrayList<>(processingLog);
    }
}
