package com.techmart.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PerformanceMonitor singleton metrics.
 */
class PerformanceMonitorTest {

    private PerformanceMonitor monitor;

    @BeforeEach
    void setUp() {
        monitor = new PerformanceMonitor();
        monitor.init();
    }

    @Test
    void testRecordRequest() {
        monitor.recordRequest("TestEndpoint", 100, true);
        monitor.recordRequest("TestEndpoint", 200, true);
        monitor.recordRequest("TestEndpoint", 50, false);

        assertEquals(3, monitor.getTotalRequests());
        assertEquals(2, monitor.getSuccessfulRequests());
        assertEquals(1, monitor.getFailedRequests());
    }

    @Test
    void testAverageResponseTime() {
        monitor.recordRequest("Endpoint", 100, true);
        monitor.recordRequest("Endpoint", 200, true);
        assertEquals(150.0, monitor.getAverageResponseTimeMs(), 0.01);
    }

    @Test
    void testEndpointCounters() {
        monitor.recordRequest("ProductServlet", 50, true);
        monitor.recordRequest("CartServlet", 75, true);

        assertEquals(1, monitor.getEndpointCounters().get("ProductServlet").get());
        assertEquals(1, monitor.getEndpointCounters().get("CartServlet").get());
    }

    @Test
    void testMemoryMetrics() {
        assertTrue(monitor.getUsedHeapMemoryMb() >= 0);
        assertTrue(monitor.getMaxHeapMemoryMb() > 0);
    }

    @Test
    void testUptime() {
        assertTrue(monitor.getUptimeMs() >= 0);
    }
}
