package com.techmart.async;

import jakarta.annotation.Resource;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;
import jakarta.ejb.TimerConfig;
import jakarta.ejb.TimerService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stateless bean providing asynchronous order processing capabilities.
 *
 * Uses @Asynchronous for non-blocking order fulfillment and email notification.
 * Returns Future<String> for optional result retrieval by callers.
 *
 * Why async: Order processing (payment validation, email, shipping label generation)
 * can take seconds. Async execution frees the HTTP thread immediately, supporting
 * sub-second response times for the checkout API.
 */
@Stateless(name = "OrderProcessorBean")
public class OrderProcessorBean {

    private static final Logger LOGGER = Logger.getLogger(OrderProcessorBean.class.getName());
    private static final long PROCESSING_DELAY_MS = 1500;

    @Resource
    private TimerService timerService;

    /**
     * Asynchronously processes an order and simulates email notification.
     *
     * @param orderId   the order identifier
     * @param customerEmail recipient email address
     * @return Future containing processing result message
     */
    @Asynchronous
    public Future<String> processOrderAsync(Long orderId, String customerEmail) {
        LOGGER.info("Async processing started for order: " + orderId);

        try {
            // Simulate order processing steps
            simulatePaymentValidation(orderId);
            simulateInventoryConfirmation(orderId);
            String emailResult = simulateEmailNotification(orderId, customerEmail);

            LOGGER.info("Async processing completed for order: " + orderId);
            return new jakarta.ejb.AsyncResult<>("SUCCESS: " + emailResult);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.SEVERE, "Order processing interrupted for order: " + orderId, e);
            return new jakarta.ejb.AsyncResult<>("FAILED: Processing interrupted");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Order processing failed for order: " + orderId, e);
            return new jakarta.ejb.AsyncResult<>("FAILED: " + e.getMessage());
        }
    }

    /**
     * Schedules a timeout-based order cancellation if payment not confirmed.
     */
    public void schedulePaymentTimeout(Long orderId, long timeoutMs) {
        TimerConfig config = new TimerConfig();
        config.setInfo("PaymentTimeout-" + orderId);
        timerService.createSingleActionTimer(timeoutMs, config);
        LOGGER.info("Payment timeout scheduled for order " + orderId + " in " + timeoutMs + "ms");
    }

    @Timeout
    public void handleTimeout(Timer timer) {
        String info = (String) timer.getInfo();
        LOGGER.warning("Timeout triggered: " + info + " - initiating cancellation workflow");
    }

    private void simulatePaymentValidation(Long orderId) throws InterruptedException {
        LOGGER.fine("Validating payment for order: " + orderId);
        Thread.sleep(PROCESSING_DELAY_MS / 3);
    }

    private void simulateInventoryConfirmation(Long orderId) throws InterruptedException {
        LOGGER.fine("Confirming inventory for order: " + orderId);
        Thread.sleep(PROCESSING_DELAY_MS / 3);
    }

    private String simulateEmailNotification(Long orderId, String email) throws InterruptedException {
        LOGGER.info("Sending order confirmation email to: " + email + " for order: " + orderId);
        Thread.sleep(PROCESSING_DELAY_MS / 3);

        String subject = "TechMart Order Confirmation #" + orderId;
        String body = "Dear Customer,\n\nYour order #" + orderId +
                " has been received and is being processed.\n\nThank you for shopping at TechMart Online!";

        LOGGER.info("Email sent - Subject: " + subject);
        return "Email notification sent to " + email;
    }
}
