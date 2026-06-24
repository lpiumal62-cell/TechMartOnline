<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - TechMart Online</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <meta http-equiv="refresh" content="30">
</head>
<body class="bg-light">
    <jsp:include page="navbar.jsp"/>

    <div class="container-fluid py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="mb-0"><i class="bi bi-speedometer2"></i> Operations Dashboard</h2>
            <a href="${pageContext.request.contextPath}/performance" class="btn btn-dark">
                <i class="bi bi-graph-up"></i> Performance Details
            </a>
        </div>

        <div class="row g-3 mb-4">
            <div class="col-md-3">
                <div class="card text-white bg-primary shadow-sm">
                    <div class="card-body">
                        <h6 class="text-uppercase opacity-75">Total Requests</h6>
                        <h2 class="mb-0">${totalRequests}</h2>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-success shadow-sm">
                    <div class="card-body">
                        <h6 class="text-uppercase opacity-75">Successful</h6>
                        <h2 class="mb-0">${successfulRequests}</h2>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-danger shadow-sm">
                    <div class="card-body">
                        <h6 class="text-uppercase opacity-75">Failed</h6>
                        <h2 class="mb-0">${failedRequests}</h2>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card text-white bg-info shadow-sm">
                    <div class="card-body">
                        <h6 class="text-uppercase opacity-75">Total Orders</h6>
                        <h2 class="mb-0">${totalOrders}</h2>
                    </div>
                </div>
            </div>
        </div>

        <div class="row g-4">
            <div class="col-md-6">
                <div class="card shadow-sm h-100">
                    <div class="card-header bg-white">
                        <h5 class="mb-0"><i class="bi bi-box-seam"></i> Live Inventory</h5>
                    </div>
                    <div class="card-body p-0">
                        <table class="table table-hover mb-0">
                            <thead class="table-light">
                                <tr><th>Product</th><th>Stock Level</th><th>Status</th></tr>
                            </thead>
                            <tbody>
                                <c:forEach var="entry" items="${inventorySnapshot}">
                                    <tr>
                                        <td>${entry.key}</td>
                                        <td><strong>${entry.value}</strong></td>
                                        <td>
                                            <span class="badge bg-${entry.value > 10 ? 'success' : entry.value > 0 ? 'warning text-dark' : 'danger'}">
                                                ${entry.value > 10 ? 'In Stock' : entry.value > 0 ? 'Low Stock' : 'Out of Stock'}
                                            </span>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card shadow-sm h-100">
                    <div class="card-header bg-white">
                        <h5 class="mb-0"><i class="bi bi-activity"></i> Performance Summary</h5>
                    </div>
                    <div class="card-body">
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item d-flex justify-content-between">
                                <span>Avg Response Time</span><strong>${avgResponseTime} ms</strong>
                            </li>
                            <li class="list-group-item d-flex justify-content-between">
                                <span>Throughput</span><strong>${throughput} req/s</strong>
                            </li>
                            <li class="list-group-item d-flex justify-content-between">
                                <span>Uptime</span><strong>${uptimeMs} ms</strong>
                            </li>
                            <li class="list-group-item d-flex justify-content-between">
                                <span>Heap Memory</span><strong>${usedHeapMb} / ${maxHeapMb} MB</strong>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <div class="row g-4 mt-1">
            <div class="col-md-6">
                <div class="card shadow-sm">
                    <div class="card-header bg-white">
                        <h5 class="mb-0"><i class="bi bi-broadcast"></i> JMS Topic — Inventory Updates</h5>
                    </div>
                    <div class="card-body" style="max-height:300px;overflow-y:auto">
                        <c:forEach var="msg" items="${jmsLog}">
                            <div class="small border-bottom py-2">${msg}</div>
                        </c:forEach>
                        <c:if test="${empty jmsLog}"><p class="text-muted mb-0">No messages yet. Place an order to see inventory broadcasts.</p></c:if>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card shadow-sm">
                    <div class="card-header bg-white">
                        <h5 class="mb-0"><i class="bi bi-inbox"></i> MDB — Order Processing Log</h5>
                    </div>
                    <div class="card-body" style="max-height:300px;overflow-y:auto">
                        <c:forEach var="msg" items="${mdbLog}">
                            <div class="small border-bottom py-2">${msg}</div>
                        </c:forEach>
                        <c:if test="${empty mdbLog}"><p class="text-muted mb-0">No orders processed yet.</p></c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
