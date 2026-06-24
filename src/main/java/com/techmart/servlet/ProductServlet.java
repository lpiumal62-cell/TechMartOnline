package com.techmart.servlet;

import com.techmart.entity.Product;
import com.techmart.stateless.ProductServiceBean;
import com.techmart.util.PerformanceMonitor;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Servlet handling product catalog display and search.
 * Demonstrates @EJB injection in web tier.
 */
@WebServlet(name = "ProductServlet", urlPatterns = {"/products", "/products/*"})
public class ProductServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ProductServlet.class.getName());

    @EJB
    private ProductServiceBean productService;

    @EJB
    private PerformanceMonitor performanceMonitor;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();
        boolean success = true;

        try {
            String action = request.getParameter("action");

            if ("detail".equals(action)) {
                Long id = Long.parseLong(request.getParameter("id"));
                productService.getProductById(id).ifPresent(p ->
                        request.setAttribute("product", p));
                request.getRequestDispatcher("/jsp/products.jsp").forward(request, response);
                return;
            }

            List<Product> products;
            String search = request.getParameter("search");
            if (search != null && !search.isBlank()) {
                products = productService.searchProducts(search);
                request.setAttribute("search", search);
            } else {
                products = productService.getAvailableProducts();
            }
            request.setAttribute("products", products);
            request.getRequestDispatcher("/jsp/products.jsp").forward(request, response);

        } catch (Exception e) {
            success = false;
            LOGGER.severe("ProductServlet error: " + e.getMessage());
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/jsp/products.jsp").forward(request, response);
        } finally {
            performanceMonitor.recordRequest("ProductServlet", System.currentTimeMillis() - start, success);
        }
    }
}
