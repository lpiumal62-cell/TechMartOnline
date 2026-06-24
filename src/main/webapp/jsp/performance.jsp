<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Performance Metrics - TechMart Online</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">

    <meta http-equiv="refresh" content="10">

    <style>

        .metric-card { transition: transform 0.2s; }

        .metric-card:hover { transform: translateY(-3px); }

    </style>

</head>

<body class="bg-dark text-white">

    <nav class="navbar navbar-dark bg-black border-bottom border-secondary">

        <div class="container">

            <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">

                <i class="bi bi-arrow-left"></i> Back to Dashboard

            </a>

            <span class="text-muted small"><i class="bi bi-arrow-repeat"></i> Auto-refresh 10s</span>

        </div>

    </nav>



    <div class="container py-4">

        <h2 class="mb-4"><i class="bi bi-graph-up"></i> Performance Monitoring</h2>



        <div class="row g-4 mb-4">

            <div class="col-md-3">

                <div class="card bg-primary text-white metric-card">

                    <div class="card-body text-center">

                        <h6 class="opacity-75">Avg Response</h6>

                        <h1 class="mb-0">${avgResponseTime}<small class="fs-6"> ms</small></h1>

                    </div>

                </div>

            </div>

            <div class="col-md-3">

                <div class="card bg-success text-white metric-card">

                    <div class="card-body text-center">

                        <h6 class="opacity-75">Throughput</h6>

                        <h1 class="mb-0">${throughput}<small class="fs-6"> req/s</small></h1>

                    </div>

                </div>

            </div>

            <div class="col-md-3">

                <div class="card bg-warning text-dark metric-card">

                    <div class="card-body text-center">

                        <h6>Memory</h6>

                        <h1 class="mb-0">${usedHeapMb}<small class="fs-6">/${maxHeapMb} MB</small></h1>

                    </div>

                </div>

            </div>

            <div class="col-md-3">

                <div class="card bg-info text-white metric-card">

                    <div class="card-body text-center">

                        <h6 class="opacity-75">Total Orders</h6>

                        <h1 class="mb-0">${totalOrders}</h1>

                    </div>

                </div>

            </div>

        </div>



        <div class="card bg-secondary text-white mb-4">

            <div class="card-header border-0"><h5 class="mb-0">Request Counters by Endpoint</h5></div>

            <div class="card-body p-0">

                <table class="table table-dark table-striped mb-0">

                    <thead>

                        <tr>

                            <th>Endpoint</th>

                            <th>Requests</th>

                            <th>Total Time (ms)</th>

                            <th>Avg Time (ms)</th>

                        </tr>

                    </thead>

                    <tbody>

                        <c:forEach var="entry" items="${endpointCounters}">

                            <tr>

                                <td><code>${entry.key}</code></td>

                                <td>${entry.value}</td>

                                <td>${endpointTiming[entry.key]}</td>

                                <td>

                                    <c:if test="${entry.value > 0}">

                                        <fmt:formatNumber value="${endpointTiming[entry.key] / entry.value}" maxFractionDigits="1"/>

                                    </c:if>

                                </td>

                            </tr>

                        </c:forEach>

                        <c:if test="${empty endpointCounters}">

                            <tr><td colspan="4" class="text-center text-muted">No requests recorded yet. Browse the site to generate metrics.</td></tr>

                        </c:if>

                    </tbody>

                </table>

            </div>

        </div>



        <div class="row g-4">

            <div class="col-md-6">

                <div class="card bg-secondary text-white h-100">

                    <div class="card-header border-0"><h5 class="mb-0">Enterprise Features</h5></div>

                    <div class="card-body">

                        <ul class="list-group list-group-flush">

                            <li class="list-group-item bg-secondary text-white d-flex justify-content-between">

                                Connection Pooling <span class="badge bg-success">Active</span>

                            </li>

                            <li class="list-group-item bg-secondary text-white d-flex justify-content-between">

                                Stateful Shopping Cart <span class="badge bg-success">EJB @Stateful</span>

                            </li>

                            <li class="list-group-item bg-secondary text-white d-flex justify-content-between">

                                JMS Messaging <span class="badge bg-success">Queue + Topic</span>

                            </li>

                            <li class="list-group-item bg-secondary text-white d-flex justify-content-between">

                                Message-Driven Bean <span class="badge bg-success">OrderMDB</span>

                            </li>

                            <li class="list-group-item bg-secondary text-white d-flex justify-content-between">

                                Async Processing <span class="badge bg-success">@Asynchronous</span>

                            </li>

                            <li class="list-group-item bg-secondary text-white d-flex justify-content-between">

                                Inventory Cache <span class="badge bg-success">@Singleton</span>

                            </li>

                        </ul>

                    </div>

                </div>

            </div>

            <div class="col-md-6">

                <div class="card bg-secondary text-white h-100">

                    <div class="card-header border-0"><h5 class="mb-0">System Health</h5></div>

                    <div class="card-body">

                        <div class="mb-4">

                            <label class="form-label">Success Rate (${successfulRequests}/${totalRequests})</label>

                            <div class="progress" style="height:28px">

                                <c:set var="successRate" value="${totalRequests > 0 ? (successfulRequests * 100 / totalRequests) : 100}"/>

                                <div class="progress-bar bg-success" style="width:${successRate}%">${successRate}%</div>

                            </div>

                        </div>

                        <div class="mb-4">

                            <label class="form-label">Memory Utilization</label>

                            <div class="progress" style="height:28px">

                                <c:set var="memRate" value="${maxHeapMb > 0 ? (usedHeapMb * 100 / maxHeapMb) : 0}"/>

                                <div class="progress-bar bg-warning text-dark" style="width:${memRate}%">${memRate}%</div>

                            </div>

                        </div>

                        <p><i class="bi bi-clock"></i> Server uptime: <strong>${uptimeMs} ms</strong></p>

                        <p class="mb-0"><i class="bi bi-x-circle"></i> Failed requests: <strong>${failedRequests}</strong></p>

                    </div>

                </div>

            </div>

        </div>

    </div>

</body>

</html>

