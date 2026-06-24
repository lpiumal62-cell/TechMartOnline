<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout - TechMart Online</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
</head>
<body class="bg-light">
    <jsp:include page="navbar.jsp"/>

    <div class="container py-4">
        <h2 class="mb-4"><i class="bi bi-credit-card"></i> Checkout</h2>

        <c:if test="${not empty sessionScope.flashMessage}">
            <div class="alert alert-success">${sessionScope.flashMessage}</div>
            <c:remove var="flashMessage" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.flashError}">
            <div class="alert alert-danger">${sessionScope.flashError}</div>
            <c:remove var="flashError" scope="session"/>
        </c:if>

        <div class="row g-4">
            <c:if test="${not empty cartItems}">
                <div class="col-lg-4 order-lg-2">
                    <div class="card shadow-sm border-success">
                        <div class="card-header bg-success text-white">
                            <h5 class="mb-0"><i class="bi bi-cart-check"></i> Order Summary</h5>
                        </div>
                        <div class="card-body">
                            <ul class="list-group list-group-flush mb-3">
                                <c:forEach var="entry" items="${cartItems}">
                                    <c:set var="product" value="${productMap[entry.key]}"/>
                                    <li class="list-group-item d-flex justify-content-between px-0">
                                        <span>${product.name} x${entry.value}</span>
                                        <strong><fmt:formatNumber value="${subtotals[entry.key]}" type="currency" currencySymbol="$"/></strong>
                                    </li>
                                </c:forEach>
                            </ul>
                            <div class="d-flex justify-content-between fs-5 fw-bold text-success">
                                <span>Total</span>
                                <span><fmt:formatNumber value="${cartTotal}" type="currency" currencySymbol="$"/></span>
                            </div>
                            <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-secondary btn-sm mt-3 w-100">Edit Cart</a>
                        </div>
                    </div>
                </div>
            </c:if>

            <div class="${not empty cartItems ? 'col-lg-8 order-lg-1' : 'col-12'}">
                <c:choose>
                    <c:when test="${not empty customer}">
                        <div class="card shadow-sm mb-4">
                            <div class="card-header bg-primary text-white">
                                <h5 class="mb-0"><i class="bi bi-person-check"></i> Welcome, ${customer.name}</h5>
                            </div>
                            <div class="card-body">
                                <p><i class="bi bi-envelope"></i> ${customer.email}</p>
                                <c:choose>
                                    <c:when test="${not empty cartItems}">
                                        <form action="${pageContext.request.contextPath}/checkout" method="post">
                                            <input type="hidden" name="action" value="placeOrder">
                                            <button type="submit" class="btn btn-success btn-lg w-100">
                                                <i class="bi bi-bag-check"></i> Place Order —
                                                <fmt:formatNumber value="${cartTotal}" type="currency" currencySymbol="$"/>
                                            </button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="alert alert-warning mb-0">
                                            Your cart is empty. <a href="${pageContext.request.contextPath}/products">Add products</a> before placing an order.
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-secondary mt-2">Logout</a>
                            </div>
                        </div>

                        <div class="card shadow-sm">
                            <div class="card-header"><h5 class="mb-0"><i class="bi bi-clock-history"></i> Order History</h5></div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${not empty orders}">
                                        <c:forEach var="order" items="${orders}">
                                            <div class="border rounded p-3 mb-3">
                                                <div class="d-flex justify-content-between align-items-center mb-2">
                                                    <strong>Order #${order.id}</strong>
                                                    <span class="badge bg-info">${order.status}</span>
                                                </div>
                                                <small class="text-muted">${order.orderDate}</small>
                                                <p class="mb-2 mt-1 fw-bold text-primary">
                                                    Total: <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="$"/>
                                                </p>
                                                <c:if test="${not empty order.items}">
                                                    <ul class="list-unstyled small mb-0">
                                                        <c:forEach var="item" items="${order.items}">
                                                            <li>• ${item.product.name} x${item.quantity} —
                                                                <fmt:formatNumber value="${item.price}" type="currency" currencySymbol="$"/> each
                                                            </li>
                                                        </c:forEach>
                                                    </ul>
                                                </c:if>
                                            </div>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise><p class="text-muted mb-0">No orders yet.</p></c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="row g-4">
                            <div class="col-md-6">
                                <div class="card shadow-sm h-100">
                                    <div class="card-header bg-primary text-white"><h5 class="mb-0">Login</h5></div>
                                    <div class="card-body">
                                        <p class="text-muted small">Demo: john.smith@email.com / password123</p>
                                        <form action="${pageContext.request.contextPath}/login" method="post">
                                            <div class="mb-3">
                                                <label class="form-label">Email</label>
                                                <input type="email" name="email" class="form-control" required>
                                            </div>
                                            <div class="mb-3">
                                                <label class="form-label">Password</label>
                                                <input type="password" name="password" class="form-control" required>
                                            </div>
                                            <button type="submit" class="btn btn-primary w-100">Login</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="card shadow-sm h-100">
                                    <div class="card-header bg-success text-white"><h5 class="mb-0">Register</h5></div>
                                    <div class="card-body">
                                        <form action="${pageContext.request.contextPath}/checkout" method="post">
                                            <input type="hidden" name="action" value="register">
                                            <div class="mb-3">
                                                <label class="form-label">Name</label>
                                                <input type="text" name="name" class="form-control" required>
                                            </div>
                                            <div class="mb-3">
                                                <label class="form-label">Email</label>
                                                <input type="email" name="email" class="form-control" required>
                                            </div>
                                            <div class="mb-3">
                                                <label class="form-label">Password</label>
                                                <input type="password" name="password" class="form-control" required>
                                            </div>
                                            <button type="submit" class="btn btn-success w-100">Create Account</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</body>
</html>
