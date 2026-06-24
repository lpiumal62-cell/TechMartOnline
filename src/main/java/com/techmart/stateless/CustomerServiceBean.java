package com.techmart.stateless;

import com.techmart.dao.CustomerDAO;
import com.techmart.entity.Customer;
import com.techmart.util.PerformanceTracked;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Stateless Session Bean for customer management and authentication.
 *
 * Why Stateless: Customer CRUD and login are independent operations.
 * Pooled instances handle high concurrent authentication requests efficiently.
 */
@Stateless(name = "CustomerServiceBean")
@PerformanceTracked
public class CustomerServiceBean {

    private static final Logger LOGGER = Logger.getLogger(CustomerServiceBean.class.getName());

    @EJB
    private CustomerDAO customerDAO;

    @PostConstruct
    public void init() {
        LOGGER.info("CustomerServiceBean initialized");
    }

    @PreDestroy
    public void destroy() {
        LOGGER.info("CustomerServiceBean destroyed");
    }

    public Customer registerCustomer(String name, String email, String password) {
        if (customerDAO.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        Customer customer = new Customer(name, email, password);
        return customerDAO.create(customer);
    }

    public Optional<Customer> login(String email, String password) {
        return customerDAO.findByEmail(email)
                .filter(c -> c.getPassword().equals(password));
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerDAO.findById(id);
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }
}
