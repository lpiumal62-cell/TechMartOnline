package com.techmart.servlet;

import com.techmart.messaging.JMSConsumer;
import com.techmart.mdb.OrderMDB;
import com.techmart.singleton.InventoryManagerBean;
import com.techmart.stateless.OrderServiceBean;
import com.techmart.util.PerformanceMonitor;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.naming.InitialContext;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet for admin dashboard and performance metrics display.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard", "/performance"})
public class DashboardServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DashboardServlet.class.getName());

    @EJB
    private PerformanceMonitor performanceMonitor;

    @EJB
    private OrderServiceBean orderService;

    @EJB
    private InventoryManagerBean inventoryManager;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();
        String path = request.getServletPath();

        request.setAttribute("totalRequests", performanceMonitor.getTotalRequests());
        request.setAttribute("successfulRequests", performanceMonitor.getSuccessfulRequests());
        request.setAttribute("failedRequests", performanceMonitor.getFailedRequests());
        request.setAttribute("avgResponseTime", String.format("%.2f", performanceMonitor.getAverageResponseTimeMs()));
        request.setAttribute("throughput", String.format("%.2f", performanceMonitor.getThroughputPerSecond()));
        request.setAttribute("uptimeMs", performanceMonitor.getUptimeMs());
        request.setAttribute("usedHeapMb", performanceMonitor.getUsedHeapMemoryMb());
        request.setAttribute("maxHeapMb", performanceMonitor.getMaxHeapMemoryMb());
        request.setAttribute("endpointCounters", performanceMonitor.getEndpointCounters());
        request.setAttribute("endpointTiming", performanceMonitor.getEndpointTiming());

        request.setAttribute("totalOrders", orderService.getTotalOrderCount());
        request.setAttribute("inventorySnapshot", inventoryManager.getNamedInventorySnapshot());
        request.setAttribute("jmsLog", lookupJmsLog());
        request.setAttribute("mdbLog", OrderMDB.getProcessingLog());

        performanceMonitor.recordRequest("DashboardServlet", System.currentTimeMillis() - start, true);

        if ("/performance".equals(path)) {
            request.getRequestDispatcher("/jsp/performance.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/jsp/dashboard.jsp").forward(request, response);
        }
    }

    private List<String> lookupJmsLog() {
        try {
            JMSConsumer consumer = (JMSConsumer) new InitialContext().lookup(
                    "java:global/TechMartOnline/JMSConsumer!com.techmart.messaging.JMSConsumer");
            return consumer.getMessageLog();
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "JMSConsumer lookup failed", e);
            return Collections.emptyList();
        }
    }
}
