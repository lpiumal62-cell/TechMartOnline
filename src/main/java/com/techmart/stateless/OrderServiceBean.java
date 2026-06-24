package com.techmart.stateless;

import com.techmart.async.OrderProcessorBean;
import com.techmart.dao.CustomerDAO;
import com.techmart.dao.OrderDAO;
import com.techmart.dao.ProductDAO;
import com.techmart.entity.*;
import com.techmart.entity.Order.OrderStatus;
import com.techmart.messaging.JMSProducer;
import com.techmart.singleton.InventoryManagerBean;
import com.techmart.util.PerformanceTracked;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * Stateless Session Bean orchestrating order placement and lifecycle.
 *
 * Why Stateless: Order placement is a discrete transaction. The bean coordinates
 * DAOs, inventory, JMS, and async processing without retaining client state.
 */
@Stateless(name = "OrderServiceBean")
@PerformanceTracked
public class OrderServiceBean {

    private static final Logger LOGGER = Logger.getLogger(OrderServiceBean.class.getName());

    @EJB
    private OrderDAO orderDAO;

    @EJB
    private CustomerDAO customerDAO;

    @EJB
    private ProductDAO productDAO;

    @EJB
    private InventoryManagerBean inventoryManager;

    @EJB
    private OrderProcessorBean orderProcessor;

    @Inject
    private JMSProducer jmsProducer;

    @PostConstruct
    public void init() {
        LOGGER.info("OrderServiceBean initialized - order pipeline ready");
    }

    @PreDestroy
    public void destroy() {
        LOGGER.info("OrderServiceBean destroyed");
    }

    /**
     * Places an order from shopping cart items.
     * Validates inventory, persists order, sends JMS message for async processing.
     */
    public Order placeOrder(Long customerId, Map<Long, Integer> cartItems) {
        Customer customer = customerDAO.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Long productId = entry.getKey();
            int quantity = entry.getValue();

            Product product = productDAO.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

            if (!inventoryManager.reserveStock(productId, quantity)) {
                throw new IllegalStateException("Insufficient stock for: " + product.getName());
            }

            OrderItem item = new OrderItem(product, quantity, product.getPrice());
            order.addItem(item);
            total = total.add(item.getSubtotal());
        }

        order.setTotalAmount(total);
        Order savedOrder = orderDAO.create(order);

        // Send to JMS queue for MDB processing
        jmsProducer.sendOrderMessage(savedOrder.getId(), customer.getEmail(), total.toString());

        // Broadcast inventory update via topic
        jmsProducer.broadcastInventoryUpdate("Order placed - Order ID: " + savedOrder.getId());

        // Trigger async email notification
        Future<String> notificationFuture = orderProcessor.processOrderAsync(savedOrder.getId(), customer.getEmail());
        LOGGER.info("Async order processing initiated for order: " + savedOrder.getId());

        return savedOrder;
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderDAO.findByCustomerId(customerId);
    }

    public List<Order> getAllOrders() {
        return orderDAO.findAll();
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.setStatus(status);
        return orderDAO.update(order);
    }

    public long getTotalOrderCount() {
        return orderDAO.countOrders();
    }
}
