package com.techmart.dao;

import com.techmart.entity.Product;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Data Access Object for Product entity.
 * Stateless EJB provides container-managed transactions and connection pooling.
 *
 * JNDI lookup example:
 *   InitialContext ctx = new InitialContext();
 *   ProductDAO dao = (ProductDAO) ctx.lookup("java:global/TechMartOnline/ProductDAO!com.techmart.dao.ProductDAO");
 */
@Stateless(name = "ProductDAO")
public class ProductDAO {

    private static final Logger LOGGER = Logger.getLogger(ProductDAO.class.getName());

    @PersistenceContext(unitName = "TechMartPU")
    private EntityManager em;

    public Product create(Product product) {
        em.persist(product);
        LOGGER.fine("Created product: " + product.getName());
        return product;
    }

    public Product update(Product product) {
        return em.merge(product);
    }

    public void delete(Long id) {
        Product product = em.find(Product.class, id);
        if (product != null) {
            em.remove(product);
        }
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(em.find(Product.class, id));
    }

    public List<Product> findAll() {
        TypedQuery<Product> query = em.createNamedQuery("Product.findAll", Product.class);
        return query.getResultList();
    }

    public List<Product> findInStock() {
        return em.createNamedQuery("Product.findInStock", Product.class).getResultList();
    }

    public List<Product> searchByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        String pattern = "%" + keyword.trim().toLowerCase() + "%";
        return em.createQuery(
                "SELECT p FROM Product p WHERE LOWER(p.name) LIKE :q OR LOWER(p.description) LIKE :q ORDER BY p.name",
                Product.class)
                .setParameter("q", pattern)
                .getResultList();
    }

    /**
     * Atomically decrements product quantity for inventory synchronization.
     */
    public boolean decrementStock(Long productId, int quantity) {
        Product product = em.find(Product.class, productId);
        if (product == null || product.getQuantity() < quantity) {
            return false;
        }
        product.setQuantity(product.getQuantity() - quantity);
        em.merge(product);
        return true;
    }
}
