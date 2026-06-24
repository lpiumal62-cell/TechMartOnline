package com.techmart.messaging;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JMS Producer for point-to-point (OrderQueue) and publish-subscribe (InventoryTopic) messaging.
 *
 * Queue (Point-to-Point): Order messages sent to exactly one consumer (OrderMDB).
 * Topic (Pub-Sub): Inventory updates broadcast to all subscribers.
 *
 * JNDI resources:
 *   java:/jms/queue/OrderQueue
 *   java:/jms/topic/InventoryTopic
 *   java:/ConnectionFactory
 */
@Stateless(name = "JMSProducer")
public class JMSProducer {

    private static final Logger LOGGER = Logger.getLogger(JMSProducer.class.getName());

    public static final String ORDER_QUEUE_JNDI = "jms/queue/OrderQueue";
    public static final String INVENTORY_TOPIC_JNDI = "jms/topic/InventoryTopic";
    public static final String CONNECTION_FACTORY_JNDI = "jms/ConnectionFactory";

    @Resource(lookup = CONNECTION_FACTORY_JNDI)
    private ConnectionFactory connectionFactory;

    @Resource(lookup = ORDER_QUEUE_JNDI)
    private Queue orderQueue;

    @Resource(lookup = INVENTORY_TOPIC_JNDI)
    private Topic inventoryTopic;

    /**
     * Sends order message to OrderQueue (point-to-point).
     * Format: orderId|customerEmail|totalAmount
     */
    public void sendOrderMessage(Long orderId, String customerEmail, String totalAmount) {
        String messageBody = orderId + "|" + customerEmail + "|" + totalAmount;

        try (JMSContext context = connectionFactory.createContext()) {
            jakarta.jms.JMSProducer producer = context.createProducer();
            TextMessage message = context.createTextMessage(messageBody);
            message.setStringProperty("orderId", String.valueOf(orderId));
            message.setStringProperty("messageType", "ORDER_PLACED");
            producer.send(orderQueue, message);
            LOGGER.info("Order message sent to queue - Order ID: " + orderId);
        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Failed to send order message for order: " + orderId, e);
            throw new RuntimeException("JMS send failed", e);
        }
    }

    /**
     * Broadcasts inventory update to InventoryTopic (publish-subscribe).
     */
    public void broadcastInventoryUpdate(String updateMessage) {
        try (JMSContext context = connectionFactory.createContext()) {
            jakarta.jms.JMSProducer producer = context.createProducer();
            TextMessage message = context.createTextMessage(updateMessage);
            message.setStringProperty("messageType", "INVENTORY_UPDATE");
            message.setLongProperty("timestamp", System.currentTimeMillis());
            producer.send(inventoryTopic, message);
            LOGGER.info("Inventory update broadcast: " + updateMessage);
        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Failed to broadcast inventory update", e);
        }
    }
}
