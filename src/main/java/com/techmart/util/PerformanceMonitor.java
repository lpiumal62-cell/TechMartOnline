package com.techmart.util;

import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Singleton bean for application-wide performance monitoring.
 * Tracks request counts, execution times, throughput, and memory usage.
 *
 * Why Singleton: Single shared instance across the cluster node ensures
 * consistent metrics aggregation without duplicate counters.
 */
@Singleton(name = "PerformanceMonitor")
@Startup
public class PerformanceMonitor {

    private static final Logger LOGGER = Logger.getLogger(PerformanceMonitor.class.getName());

    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong successfulRequests = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);
    private final AtomicLong totalExecutionTimeMs = new AtomicLong(0);
    private final ConcurrentHashMap<String, AtomicLong> endpointCounters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> endpointTiming = new ConcurrentHashMap<>();

    private long startupTime;
    private MemoryMXBean memoryBean;

    @PostConstruct
    public void init() {
        startupTime = System.currentTimeMillis();
        memoryBean = ManagementFactory.getMemoryMXBean();
        LOGGER.info("PerformanceMonitor initialized at " + startupTime);
    }

    @PreDestroy
    public void destroy() {
        LOGGER.info("PerformanceMonitor shutting down. Total requests: " + totalRequests.get());
    }

    public void recordRequest(String endpoint, long executionTimeMs, boolean success) {
        totalRequests.incrementAndGet();
        totalExecutionTimeMs.addAndGet(executionTimeMs);
        endpointCounters.computeIfAbsent(endpoint, k -> new AtomicLong(0)).incrementAndGet();
        endpointTiming.computeIfAbsent(endpoint, k -> new AtomicLong(0)).addAndGet(executionTimeMs);

        if (success) {
            successfulRequests.incrementAndGet();
        } else {
            failedRequests.incrementAndGet();
        }
    }

    public long getTotalRequests() {
        return totalRequests.get();
    }

    public long getSuccessfulRequests() {
        return successfulRequests.get();
    }

    public long getFailedRequests() {
        return failedRequests.get();
    }

    public double getAverageResponseTimeMs() {
        long total = totalRequests.get();
        return total > 0 ? (double) totalExecutionTimeMs.get() / total : 0.0;
    }

    public double getThroughputPerSecond() {
        long uptimeSeconds = (System.currentTimeMillis() - startupTime) / 1000;
        return uptimeSeconds > 0 ? (double) totalRequests.get() / uptimeSeconds : 0.0;
    }

    public long getUptimeMs() {
        return System.currentTimeMillis() - startupTime;
    }

    public long getUsedHeapMemoryMb() {
        return memoryBean.getHeapMemoryUsage().getUsed() / (1024 * 1024);
    }

    public long getMaxHeapMemoryMb() {
        return memoryBean.getHeapMemoryUsage().getMax() / (1024 * 1024);
    }

    public ConcurrentHashMap<String, AtomicLong> getEndpointCounters() {
        return endpointCounters;
    }

    public ConcurrentHashMap<String, AtomicLong> getEndpointTiming() {
        return endpointTiming;
    }
}
