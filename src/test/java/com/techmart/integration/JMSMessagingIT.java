package com.techmart.integration;

import com.techmart.messaging.JMSProducer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Arquillian integration tests for JMS messaging components.
 * Requires WildFly with JMS configured (standalone-full.xml profile).
 */
@ExtendWith(ArquillianExtension.class)
class JMSMessagingIT {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "jms-test.war")
                .addPackages(true, "com.techmart")
                .addAsResource("META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private JMSProducer jmsProducer;

    @Test
    void testSendOrderMessage() {
        assertDoesNotThrow(() ->
                jmsProducer.sendOrderMessage(999L, "test@email.com", "100.00"));
    }

    @Test
    void testBroadcastInventoryUpdate() {
        assertDoesNotThrow(() ->
                jmsProducer.broadcastInventoryUpdate("Test inventory update from IT"));
    }
}
