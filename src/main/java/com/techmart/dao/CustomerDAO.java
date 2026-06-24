package com.techmart.dao;

import com.techmart.entity.Customer;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Data Access Object for Customer entity.
 *
 * JNDI lookup example:
 *   CustomerDAO dao = (CustomerDAO) ctx.lookup("java:global/TechMartOnline/CustomerDAO!com.techmart.dao.CustomerDAO");
 */
@Stateless(name = "CustomerDAO")
public class CustomerDAO {

    private static final Logger LOGGER = Logger.getLogger(CustomerDAO.class.getName());

    @PersistenceContext(unitName = "TechMartPU")
    private EntityManager em;

    public Customer create(Customer customer) {
        em.persist(customer);
        LOGGER.fine("Created customer: " + customer.getEmail());
        return customer;
    }

    public Customer update(Customer customer) {
        return em.merge(customer);
    }

    public Optional<Customer> findById(Long id) {
        return Optional.ofNullable(em.find(Customer.class, id));
    }

    public Optional<Customer> findByEmail(String email) {
        try {
            Customer customer = em.createNamedQuery("Customer.findByEmail", Customer.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(customer);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Customer> findAll() {
        return em.createNamedQuery("Customer.findAll", Customer.class).getResultList();
    }

    public boolean authenticate(String email, String password) {
        return findByEmail(email)
                .map(c -> c.getPassword().equals(password))
                .orElse(false);
    }
}
