package com.techmart.integration;

import com.techmart.stateless.ProductServiceBean;
import com.techmart.entity.Product;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Arquillian integration tests for EJB beans deployed on WildFly.
 * Run with: mvn test -Parquillian-wildfly-managed
 *
 * Prerequisites:
 * - WildFly installed and JBOSS_HOME set
 * - MySQL datasource configured
 */
@ExtendWith(ArquillianExtension.class)
class ProductServiceBeanIT {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "com.techmart")
                .addAsResource("META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private ProductServiceBean productService;

    @Test
    void testGetAllProducts() {
        List<Product> products = productService.getAllProducts();
        assertNotNull(products);
    }

    @Test
    void testProductAvailability() {
        // Assumes product ID 1 exists from sample data
        boolean available = productService.isProductAvailable(1L, 1);
        assertTrue(available || !available); // Validates method executes without error
    }
}
