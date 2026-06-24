package com.techmart.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JPA entity business logic.
 */
class ProductTest {

    @Test
    void testProductCreation() {
        Product product = new Product("Test Laptop", "A test product", new BigDecimal("999.99"), 10);
        assertEquals("Test Laptop", product.getName());
        assertEquals(new BigDecimal("999.99"), product.getPrice());
        assertEquals(10, product.getQuantity());
        assertTrue(product.isInStock());
    }

    @Test
    void testOutOfStock() {
        Product product = new Product("Sold Out Item", "No stock", new BigDecimal("50.00"), 0);
        assertFalse(product.isInStock());
    }

    @Test
    void testOrderItemSubtotal() {
        Product product = new Product("Mouse", "Wireless mouse", new BigDecimal("29.99"), 100);
        OrderItem item = new OrderItem(product, 3, product.getPrice());
        assertEquals(new BigDecimal("89.97"), item.getSubtotal());
    }

    @Test
    void testOrderStatusDefault() {
        Order order = new Order();
        assertNull(order.getStatus());
    }

    @Test
    void testCustomerCreation() {
        Customer customer = new Customer("Test User", "test@email.com", "pass123");
        assertEquals("test@email.com", customer.getEmail());
        assertNotNull(customer.getOrders());
        assertTrue(customer.getOrders().isEmpty());
    }
}
