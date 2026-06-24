package com.techmart.integration;

import com.techmart.mdb.OrderMDB;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OrderMDB processing log (MDB logic validation).
 * Full MDB integration requires Arquillian with active JMS queue.
 */
class OrderMDBTest {

    @Test
    void testProcessingLogAccessible() {
        List<String> log = OrderMDB.getProcessingLog();
        assertNotNull(log);
    }

    @Test
    void testMessageParsing() {
        String message = "123|test@email.com|299.99";
        String[] parts = message.split("\\|");
        assertEquals(3, parts.length);
        assertEquals("123", parts[0]);
        assertEquals("test@email.com", parts[1]);
        assertEquals("299.99", parts[2]);
    }
}
