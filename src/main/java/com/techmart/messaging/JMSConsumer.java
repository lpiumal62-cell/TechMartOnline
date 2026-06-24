package com.techmart.messaging;

import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.jms.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JMS Consumer subscribing to InventoryTopic for real-time inventory notifications.
 * Maintains a log of received messages for dashboard display.
 *
 * Demonstrates publish-subscribe pattern: multiple consumers can receive
 * the same inventory broadcast simultaneously.
 */
@Singleton(name = "JMSConsumer")
@Startup
public class JMSConsumer implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(JMSConsumer.class.getName());
    private static final int MAX_LOG_SIZE = 100;

    @Resource(lookup = JMSProducer.CONNECTION_FACTORY_JNDI)
    private ConnectionFactory connectionFactory;

    @Resource(lookup = JMSProducer.INVENTORY_TOPIC_JNDI)
    private Topic inventoryTopic;

    private final List<String> messageLog = new CopyOnWriteArrayList<>();
    private Connection connection;
    private Session session;

    @jakarta.annotation.PostConstruct
    public void init() {
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(inventoryTopic);
            consumer.setMessageListener(this);
            connection.start();
            LOGGER.info("JMSConsumer subscribed to InventoryTopic");
        } catch (JMSException e) {
            LOGGER.log(Level.WARNING, "JMSConsumer could not subscribe to topic - JMS may not be configured yet", e);
        }
    }

    @jakarta.annotation.PreDestroy
    public void destroy() {
        try {
            if (session != null) session.close();
            if (connection != null) connection.close();
            LOGGER.info("JMSConsumer disconnected from InventoryTopic");
        } catch (JMSException e) {
            LOGGER.log(Level.WARNING, "Error closing JMS consumer", e);
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String body = textMessage.getText();
                String type = textMessage.getStringProperty("messageType");
                long timestamp = textMessage.getLongProperty("timestamp");

                String logEntry = String.format("[%d] %s: %s", timestamp, type, body);
                messageLog.add(0, logEntry);

                // Keep log bounded
                while (messageLog.size() > MAX_LOG_SIZE) {
                    messageLog.remove(messageLog.size() - 1);
                }

                LOGGER.info("Received inventory message: " + body);
            }
        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Error processing JMS message", e);
        }
    }

    /**
     * Synchronously receives a message from OrderQueue (for testing/demo).
     */
    public String receiveOrderMessage(long timeoutMs) {
        try (JMSContext context = connectionFactory.createContext()) {
            jakarta.jms.JMSConsumer jmsConsumer = context.createConsumer(
                    (Queue) context.createQueue("OrderQueue"));
            Message message = jmsConsumer.receive(timeoutMs);
            if (message instanceof TextMessage) {
                return ((TextMessage) message).getText();
            }
        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Error receiving order message", e);
        }
        return null;
    }

    public List<String> getMessageLog() {
        return Collections.unmodifiableList(new ArrayList<>(messageLog));
    }
}
