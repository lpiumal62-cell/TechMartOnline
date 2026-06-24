<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Shopping Cart - TechMart Online</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
</head>
<body class="bg-light">
    <jsp:include page="navbar.jsp"/>

    <div class="container py-4">
        <h2 class="mb-4"><i class="bi bi-cart3"></i> Shopping Cart
            <span class="badge bg-primary">${itemCount} items</span>
        </h2>

        <c:if test="${not empty sessionScope.flashMessage}">
            <div class="alert alert-success">${sessionScope.flashMessage}</div>
            <c:remove var="flashMessage" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.flashError}">
            <div class="alert alert-danger">${sessionScope.flashError}</div>
            <c:remove var="flashError" scope="session"/>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <c:choose>
            <c:when test="${not empty cartItems}">
                <div class="card shadow-sm">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead class="table-dark">
                                <tr>
                                    <th>Product</th>
                                    <th>Price</th>
                                    <th>Quantity</th>
                                    <th>Subtotal</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="entry" items="${cartItems}">
                                    <c:set var="product" value="${productMap[entry.key]}"/>
                                    <tr>
                                        <td>
                                            <strong>${product.name}</strong>
                                            <br><small class="text-muted">${product.description}</small>
                                        </td>
                                        <td><fmt:formatNumber value="${product.price}" type="currency" currencySymbol="$"/></td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/cart" method="post" class="d-inline">
                                                <input type="hidden" name="action" value="update">
                                                <input type="hidden" name="productId" value="${entry.key}">
                                                <input type="number" name="quantity" value="${entry.value}" min="1" max="${product.quantity}"
                                                       class="form-control form-control-sm" style="width:70px" onchange="this.form.submit()">
                                            </form>
                                        </td>
                                        <td><fmt:formatNumber value="${subtotals[entry.key]}" type="currency" currencySymbol="$"/></td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/cart" method="post" class="d-inline">
                                                <input type="hidden" name="action" value="remove">
                                                <input type="hidden" name="productId" value="${entry.key}">
                                                <button type="submit" class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i></button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                            <tfoot class="table-light">
                                <tr>
                                    <td colspan="3" class="text-end fw-bold fs-5">Total:</td>
                                    <td class="fw-bold text-primary fs-5">
                                        <fmt:formatNumber value="${cartTotal}" type="currency" currencySymbol="$"/>
                                    </td>
                                    <td></td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>
                <div class="d-flex justify-content-between mt-4">
                    <div>
                        <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-primary me-2">
                            <i class="bi bi-arrow-left"></i> Continue Shopping
                        </a>
                        <form action="${pageContext.request.contextPath}/cart" method="post" class="d-inline">
                            <input type="hidden" name="action" value="clear">
                            <button type="submit" class="btn btn-outline-secondary">Clear Cart</button>
                        </form>
                    </div>
                    <a href="${pageContext.request.contextPath}/checkout" class="btn btn-success btn-lg">
                        <i class="bi bi-credit-card"></i> Proceed to Checkout
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="text-center py-5">
                    <i class="bi bi-cart-x display-1 text-muted"></i>
                    <h4 class="mt-3">Your cart is empty</h4>
                    <p class="text-muted">Browse our catalog and add items to get started.</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg mt-2">
                        <i class="bi bi-grid"></i> Browse Products
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
