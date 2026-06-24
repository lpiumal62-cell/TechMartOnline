package com.techmart.stateful;

import com.techmart.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ShoppingCartBean stateful session logic.
 */
class ShoppingCartBeanTest {

    private ShoppingCartBean cart;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCartBean();
        cart.init();
    }

    @Test
    void testAddItem() {
        cart.addItem(1L, 2);
        assertEquals(2, cart.getItemCount());
        assertFalse(cart.isEmpty());
        assertEquals(2, cart.getItems().get(1L));
    }

    @Test
    void testAddSameItemIncrementsQuantity() {
        cart.addItem(1L, 1);
        cart.addItem(1L, 3);
        assertEquals(4, cart.getItems().get(1L));
        assertEquals(4, cart.getItemCount());
    }

    @Test
    void testRemoveItem() {
        cart.addItem(1L, 2);
        cart.removeItem(1L);
        assertTrue(cart.isEmpty());
    }

    @Test
    void testUpdateQuantity() {
        cart.addItem(1L, 5);
        cart.updateQuantity(1L, 2);
        assertEquals(2, cart.getItems().get(1L));
    }

    @Test
    void testUpdateQuantityToZeroRemoves() {
        cart.addItem(1L, 5);
        cart.updateQuantity(1L, 0);
        assertFalse(cart.getItems().containsKey(1L));
    }

    @Test
    void testCalculateTotal() {
        cart.addItem(1L, 2);
        cart.addItem(2L, 1);

        Map<Long, Product> productMap = new HashMap<>();
        productMap.put(1L, new Product("Item A", "Desc", new BigDecimal("10.00"), 100));
        productMap.put(2L, new Product("Item B", "Desc", new BigDecimal("25.00"), 50));

        BigDecimal total = cart.calculateTotal(productMap);
        assertEquals(new BigDecimal("45.00"), total);
    }

    @Test
    void testClearCart() {
        cart.addItem(1L, 1);
        cart.addItem(2L, 3);
        cart.clear();
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getItemCount());
    }

    @Test
    void testInvalidQuantityThrows() {
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(1L, 0));
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(1L, -1));
    }
}
