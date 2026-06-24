package com.techmart.servlet;

import com.techmart.entity.Customer;
import com.techmart.entity.Order;
import com.techmart.stateful.ShoppingCartBean;
import com.techmart.stateless.CustomerServiceBean;
import com.techmart.stateless.OrderServiceBean;
import com.techmart.stateless.ProductServiceBean;
import com.techmart.util.CartViewHelper;
import com.techmart.util.PerformanceMonitor;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Servlet handling checkout flow and customer authentication.
 */
@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout", "/checkout/*", "/login", "/logout"})
public class CheckoutServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CheckoutServlet.class.getName());
    private static final String CART_BEAN_KEY = "shoppingCartBean";

    @EJB
    private OrderServiceBean orderService;

    @EJB
    private CustomerServiceBean customerService;

    @EJB
    private ProductServiceBean productService;

    @EJB
    private PerformanceMonitor performanceMonitor;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();
        String path = request.getServletPath();

        try {
            if ("/logout".equals(path)) {
                request.getSession().invalidate();
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }

            HttpSession session = request.getSession(false);
            Customer customer = null;
            if (session != null) {
                Long customerId = (Long) session.getAttribute("customerId");
                if (customerId != null) {
                    customer = customerService.getCustomerById(customerId).orElse(null);
                }
            }

            if (customer != null) {
                List<Order> orders = orderService.getOrdersByCustomer(customer.getId());
                request.setAttribute("orders", orders);
                request.setAttribute("customer", customer);
            }

            ShoppingCartBean cart = session != null
                    ? (ShoppingCartBean) session.getAttribute(CART_BEAN_KEY) : null;
            if (cart != null && !cart.isEmpty()) {
                CartViewHelper.populateCartAttributes(request, cart, productService);
            }

            request.getRequestDispatcher("/jsp/checkout.jsp").forward(request, response);

        } finally {
            performanceMonitor.recordRequest("CheckoutServlet", System.currentTimeMillis() - start, true);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();
        boolean success = true;
        String path = request.getServletPath();
        String action = request.getParameter("action");

        try {
            if ("/login".equals(path) || "login".equals(action)) {
                handleLogin(request, response);
                return;
            }

            if ("register".equals(action)) {
                handleRegister(request, response);
                return;
            }

            if ("placeOrder".equals(action)) {
                handlePlaceOrder(request, response);
                return;
            }

            response.sendRedirect(request.getContextPath() + "/checkout");

        } catch (Exception e) {
            success = false;
            LOGGER.severe("CheckoutServlet error: " + e.getMessage());
            request.getSession().setAttribute("flashError", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/checkout");
        } finally {
            performanceMonitor.recordRequest("CheckoutServlet", System.currentTimeMillis() - start, success);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        customerService.login(email, password).ifPresentOrElse(customer -> {
            HttpSession session = request.getSession(true);
            session.setAttribute("customerId", customer.getId());
            session.setAttribute("customerName", customer.getName());
            try {
                response.sendRedirect(request.getContextPath() + "/checkout");
            } catch (IOException e) {
                LOGGER.severe("Redirect failed: " + e.getMessage());
            }
        }, () -> {
            request.getSession().setAttribute("flashError", "Invalid email or password");
            try {
                response.sendRedirect(request.getContextPath() + "/checkout");
            } catch (IOException e) {
                LOGGER.severe("Redirect failed: " + e.getMessage());
            }
        });
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            Customer customer = customerService.registerCustomer(name, email, password);
            HttpSession session = request.getSession(true);
            session.setAttribute("customerId", customer.getId());
            session.setAttribute("customerName", customer.getName());
            session.setAttribute("flashMessage", "Registration successful!");
            response.sendRedirect(request.getContextPath() + "/checkout");
        } catch (IllegalArgumentException e) {
            request.getSession().setAttribute("flashError", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/checkout");
        }
    }

    private void handlePlaceOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/checkout");
            return;
        }

        Long customerId = (Long) session.getAttribute("customerId");
        ShoppingCartBean cart = (ShoppingCartBean) session.getAttribute(CART_BEAN_KEY);

        if (customerId == null || cart == null || cart.isEmpty()) {
            session.setAttribute("flashError", "Please login and add items to cart");
            response.sendRedirect(request.getContextPath() + "/checkout");
            return;
        }

        Order order = orderService.placeOrder(customerId, cart.getItems());
        cart.checkoutComplete();
        session.removeAttribute(CART_BEAN_KEY);

        session.setAttribute("flashMessage", "Order #" + order.getId() + " placed successfully!");
        response.sendRedirect(request.getContextPath() + "/checkout");
    }
}
