package com.techmart.util;

import com.techmart.entity.Product;
import com.techmart.stateful.ShoppingCartBean;
import com.techmart.stateless.ProductServiceBean;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/** Populates request attributes for cart/checkout JSP views. */
public final class CartViewHelper {

    private CartViewHelper() {
    }

    public static void populateCartAttributes(HttpServletRequest request,
                                              ShoppingCartBean cart,
                                              ProductServiceBean productService) {
        Map<Long, Integer> items = cart.getItems();
        Map<Long, Product> productMap = new HashMap<>();
        Map<Long, BigDecimal> subtotals = new HashMap<>();

        for (Map.Entry<Long, Integer> entry : items.entrySet()) {
            productService.getProductById(entry.getKey()).ifPresent(p -> {
                productMap.put(entry.getKey(), p);
                subtotals.put(entry.getKey(), p.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
            });
        }

        request.setAttribute("cartItems", items);
        request.setAttribute("productMap", productMap);
        request.setAttribute("subtotals", subtotals);
        request.setAttribute("cartTotal", cart.calculateTotal(productMap));
        request.setAttribute("itemCount", cart.getItemCount());
        request.setAttribute("cartEmpty", cart.isEmpty());
    }
}
