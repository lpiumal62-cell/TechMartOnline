package com.techmart.singleton;

import com.techmart.dao.ProductDAO;
import com.techmart.entity.Product;
import com.techmart.messaging.JMSProducer;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Singleton Session Bean for centralized inventory management and caching.
 *
 * Why Singleton: Inventory must be synchronized across all concurrent users.
 * A single instance maintains an in-memory cache of stock levels, providing
 * sub-millisecond reads and real-time synchronization via JMS topic broadcasts.
 *
 * JNDI lookup:
 *   java:global/TechMartOnline/InventoryManagerBean!com.techmart.singleton.InventoryManagerBean
 */
@Singleton(name = "InventoryManagerBean")
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class InventoryManagerBean {

    private static final Logger LOGGER = Logger.getLogger(InventoryManagerBean.class.getName());

    @EJB
    private ProductDAO productDAO;

    @Inject
    private JMSProducer jmsProducer;

    /** In-memory inventory cache for fast lookups */
    private final Map<Long, Integer> inventoryCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        LOGGER.info("InventoryManagerBean initializing - loading inventory cache");
        refreshCache();
        LOGGER.info("Inventory cache loaded with " + inventoryCache.size() + " products");
    }

    @PreDestroy
    public void destroy() {
        inventoryCache.clear();
        LOGGER.info("InventoryManagerBean destroyed - cache cleared");
    }

    /**
     * Refreshes in-memory cache from database (connection pooling via JPA).
     */
    public void refreshCache() {
        List<Product> products = productDAO.findAll();
        inventoryCache.clear();
        for (Product p : products) {
            inventoryCache.put(p.getId(), p.getQuantity());
        }
    }

    public int getStockLevel(Long productId) {
        return inventoryCache.getOrDefault(productId, 0);
    }

    public boolean isAvailable(Long productId, int quantity) {
        return getStockLevel(productId) >= quantity;
    }

    /**
     * Reserves stock atomically in cache and database.
     * Implements optimistic concurrency for high-throughput scenarios.
     */
    public synchronized boolean reserveStock(Long productId, int quantity) {
        int current = getStockLevel(productId);
        if (current < quantity) {
            LOGGER.warning("Insufficient stock for product " + productId + ": requested=" + quantity + ", available=" + current);
            return false;
        }

        // Update cache
        inventoryCache.put(productId, current - quantity);

        // Persist to database
        boolean dbSuccess = productDAO.decrementStock(productId, quantity);
        if (!dbSuccess) {
            // Rollback cache on DB failure (circuit breaker pattern)
            inventoryCache.put(productId, current);
            return false;
        }

        // Broadcast inventory change via JMS topic
        jmsProducer.broadcastInventoryUpdate(
                "Stock updated - Product ID: " + productId + ", New quantity: " + (current - quantity));

        LOGGER.info("Reserved " + quantity + " units of product " + productId);
        return true;
    }

    public void releaseStock(Long productId, int quantity) {
        int current = getStockLevel(productId);
        inventoryCache.put(productId, current + quantity);
        Product product = productDAO.findById(productId).orElse(null);
        if (product != null) {
            product.setQuantity(product.getQuantity() + quantity);
            productDAO.update(product);
        }
        jmsProducer.broadcastInventoryUpdate("Stock released - Product ID: " + productId);
    }

    public Map<Long, Integer> getInventorySnapshot() {
        return new ConcurrentHashMap<>(inventoryCache);
    }

    /** Returns product name → stock level for dashboard display. */
    public Map<String, Integer> getNamedInventorySnapshot() {
        Map<String, Integer> snapshot = new java.util.LinkedHashMap<>();
        for (Map.Entry<Long, Integer> entry : inventoryCache.entrySet()) {
            productDAO.findById(entry.getKey())
                    .ifPresent(p -> snapshot.put(p.getName(), entry.getValue()));
        }
        return snapshot;
    }
}
