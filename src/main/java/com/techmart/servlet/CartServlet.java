package com.techmart.servlet;

import com.techmart.stateful.ShoppingCartBean;
import com.techmart.stateless.ProductServiceBean;
import com.techmart.util.CartViewHelper;
import com.techmart.util.PerformanceMonitor;

import jakarta.ejb.EJB;
import javax.naming.InitialContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Servlet managing shopping cart operations.
 * Uses Stateful ShoppingCartBean for per-session cart state.
 *
 * JNDI example for manual lookup:
 *   ShoppingCartBean cart = (ShoppingCartBean) new InitialContext()
 *       .lookup("java:global/TechMartOnline/ShoppingCartBean!com.techmart.stateful.ShoppingCartBean");
 */
@WebServlet(name = "CartServlet", urlPatterns = {"/cart", "/cart/*"})
public class CartServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CartServlet.class.getName());
    private static final String CART_BEAN_KEY = "shoppingCartBean";

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
            ShoppingCartBean cart = getOrCreateCart(request);
            populateCartAttributes(request, cart);
            request.getRequestDispatcher("/jsp/cart.jsp").forward(request, response);
        } catch (Exception e) {
            success = false;
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/jsp/cart.jsp").forward(request, response);
        } finally {
            performanceMonitor.recordRequest("CartServlet", System.currentTimeMillis() - start, success);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();
        boolean success = true;
        String action = request.getParameter("action");

        try {
            ShoppingCartBean cart = getOrCreateCart(request);

            switch (action != null ? action : "") {
                case "add":
                    Long productId = Long.parseLong(request.getParameter("productId"));
                    int quantity = Integer.parseInt(request.getParameter("quantity"));
                    if (!productService.isProductAvailable(productId, quantity)) {
                        request.getSession().setAttribute("flashError", "Insufficient stock");
                    } else {
                        cart.addItem(productId, quantity);
                        request.getSession().setAttribute("flashMessage", "Product added to cart");
                    }
                    break;

                case "update":
                    Long updateId = Long.parseLong(request.getParameter("productId"));
                    int newQty = Integer.parseInt(request.getParameter("quantity"));
                    if (newQty <= 0) {
                        cart.removeItem(updateId);
                    } else if (!productService.isProductAvailable(updateId, newQty)) {
                        request.getSession().setAttribute("flashError", "Insufficient stock for requested quantity");
                    } else {
                        cart.updateQuantity(updateId, newQty);
                        request.getSession().setAttribute("flashMessage", "Cart updated");
                    }
                    break;

                case "remove":
                    Long removeId = Long.parseLong(request.getParameter("productId"));
                    cart.removeItem(removeId);
                    break;

                case "clear":
                    cart.clear();
                    break;

                default:
                    LOGGER.warning("Unknown cart action: " + action);
            }

            response.sendRedirect(request.getContextPath() + "/cart");

        } catch (Exception e) {
            success = false;
            LOGGER.severe("CartServlet error: " + e.getMessage());
            request.getSession().setAttribute("flashError", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/cart");
        } finally {
            performanceMonitor.recordRequest("CartServlet", System.currentTimeMillis() - start, success);
        }
    }

    private ShoppingCartBean getOrCreateCart(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession(true);

        ShoppingCartBean cart = (ShoppingCartBean) session.getAttribute(CART_BEAN_KEY);
        if (cart == null) {
            // CDI/EJB lookup for stateful bean
            InitialContext ctx = new InitialContext();
            cart = (ShoppingCartBean) ctx.lookup(
                    "java:global/TechMartOnline/ShoppingCartBean!com.techmart.stateful.ShoppingCartBean");
            cart.setSessionId(session.getId());

            Long customerId = (Long) session.getAttribute("customerId");
            if (customerId != null) {
                cart.setCustomerId(customerId);
            }

            session.setAttribute(CART_BEAN_KEY, cart);
        }
        return cart;
    }

    private void populateCartAttributes(HttpServletRequest request, ShoppingCartBean cart) {
        CartViewHelper.populateCartAttributes(request, cart, productService);
    }
}
