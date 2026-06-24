package com.techmart.dao;

import com.techmart.entity.Order;
import com.techmart.entity.Order.OrderStatus;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Data Access Object for Order entity.
 *
 * JNDI lookup example:
 *   OrderDAO dao = (OrderDAO) ctx.lookup("java:global/TechMartOnline/OrderDAO!com.techmart.dao.OrderDAO");
 */
@Stateless(name = "OrderDAO")
public class OrderDAO {

    private static final Logger LOGGER = Logger.getLogger(OrderDAO.class.getName());

    @PersistenceContext(unitName = "TechMartPU")
    private EntityManager em;

    public Order create(Order order) {
        em.persist(order);
        LOGGER.info("Created order ID: " + order.getId() + " for customer: " + order.getCustomer().getName());
        return order;
    }

    public Order update(Order order) {
        return em.merge(order);
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(em.find(Order.class, id));
    }

    public List<Order> findAll() {
        return em.createNamedQuery("Order.findAll", Order.class).getResultList();
    }

    public List<Order> findByCustomerId(Long customerId) {
        return em.createNamedQuery("Order.findByCustomer", Order.class)
                .setParameter("customerId", customerId)
                .getResultList();
    }

    public List<Order> findByStatus(OrderStatus status) {
        return em.createNamedQuery("Order.findByStatus", Order.class)
                .setParameter("status", status)
                .getResultList();
    }

    public long countOrders() {
        return em.createQuery("SELECT COUNT(o) FROM Order o", Long.class).getSingleResult();
    }
}
