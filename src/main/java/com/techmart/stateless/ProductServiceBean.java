package com.techmart.stateless;

import com.techmart.dao.ProductDAO;
import com.techmart.entity.Product;
import com.techmart.util.PerformanceTracked;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Stateless Session Bean for product catalog operations.
 *
 * Why Stateless: Product operations are independent, stateless transactions.
 * The container pools instances for scalability (10,000+ concurrent users).
 * Each method call is self-contained with no client-specific state.
 *
 * JNDI lookup:
 *   java:global/TechMartOnline/ProductServiceBean!com.techmart.stateless.ProductServiceBean
 */
@Stateless(name = "ProductServiceBean")
@PerformanceTracked
public class ProductServiceBean {

    private static final Logger LOGGER = Logger.getLogger(ProductServiceBean.class.getName());

    @EJB
    private ProductDAO productDAO;

    @PostConstruct
    public void init() {
        LOGGER.info("ProductServiceBean initialized - ready to serve catalog requests");
    }

    @PreDestroy
    public void destroy() {
        LOGGER.info("ProductServiceBean destroyed - releasing resources");
    }

    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    public List<Product> getAvailableProducts() {
        return productDAO.findInStock();
    }

    public List<Product> searchProducts(String keyword) {
        return productDAO.searchByKeyword(keyword);
    }

    public Optional<Product> getProductById(Long id) {
        return productDAO.findById(id);
    }

    public Product addProduct(Product product) {
        return productDAO.create(product);
    }

    public Product updateProduct(Product product) {
        return productDAO.update(product);
    }

    public boolean isProductAvailable(Long productId, int quantity) {
        return productDAO.findById(productId)
                .map(p -> p.getQuantity() >= quantity)
                .orElse(false);
    }
}
