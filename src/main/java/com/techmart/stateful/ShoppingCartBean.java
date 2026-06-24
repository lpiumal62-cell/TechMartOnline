package com.techmart.stateful;

import com.techmart.entity.Product;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Remove;
import jakarta.ejb.Stateful;
import jakarta.ejb.StatefulTimeout;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Stateful Session Bean maintaining per-client shopping cart state.
 *
 * Why Stateful: The shopping cart is inherently client-specific state that must
 * persist across multiple HTTP requests within a session. Each customer gets a
 * dedicated bean instance bound to their session.
 *
 * JNDI lookup (requires business interface or no-interface view):
 *   java:global/TechMartOnline/ShoppingCartBean!com.techmart.stateful.ShoppingCartBean
 */
@Stateful(name = "ShoppingCartBean")
@LocalBean
@StatefulTimeout(value = 30, unit = TimeUnit.MINUTES)
public class ShoppingCartBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ShoppingCartBean.class.getName());

    /** Cart items: productId -> quantity */
    private Map<Long, Integer> items;
    private Long customerId;
    private String sessionId;

    @PostConstruct
    public void init() {
        items = new LinkedHashMap<>();
        LOGGER.info("ShoppingCartBean created for new session");
    }

    @PreDestroy
    public void destroy() {
        LOGGER.info("ShoppingCartBean destroyed - sessionId: " + sessionId + ", items: " + items.size());
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void addItem(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        items.merge(productId, quantity, Integer::sum);
        LOGGER.fine("Added product " + productId + " x" + quantity + " to cart");
    }

    public void updateQuantity(Long productId, int quantity) {
        if (quantity <= 0) {
            items.remove(productId);
        } else {
            items.put(productId, quantity);
        }
    }

    public void removeItem(Long productId) {
        items.remove(productId);
    }

    public Map<Long, Integer> getItems() {
        return new LinkedHashMap<>(items);
    }

    public int getItemCount() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
        LOGGER.info("Cart cleared for session: " + sessionId);
    }

    /**
     * Calculates cart total using provided product prices.
     */
    public BigDecimal calculateTotal(Map<Long, Product> productMap) {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            Product product = productMap.get(entry.getKey());
            if (product != null) {
                total = total.add(product.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
            }
        }
        return total;
    }

    @Remove
    public void checkoutComplete() {
        items.clear();
        LOGGER.info("Checkout complete - ShoppingCartBean removed");
    }
}
