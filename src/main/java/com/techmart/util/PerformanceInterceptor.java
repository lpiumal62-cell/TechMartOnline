package com.techmart.util;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.util.logging.Logger;

/**
 * CDI interceptor for measuring EJB method execution time.
 * Demonstrates @Inject-based cross-cutting concern implementation.
 */
@Interceptor
@PerformanceTracked
public class PerformanceInterceptor {

    private static final Logger LOGGER = Logger.getLogger(PerformanceInterceptor.class.getName());

    @Inject
    private PerformanceMonitor performanceMonitor;

    @AroundInvoke
    public Object measure(InvocationContext context) throws Exception {
        long start = System.currentTimeMillis();
        String methodName = context.getTarget().getClass().getSimpleName() + "." + context.getMethod().getName();
        boolean success = true;
        try {
            return context.proceed();
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            performanceMonitor.recordRequest(methodName, elapsed, success);
            LOGGER.fine(methodName + " executed in " + elapsed + "ms");
        }
    }
}
