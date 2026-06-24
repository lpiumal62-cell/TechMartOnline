<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Products - TechMart Online</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
</head>
<body class="bg-light">
    <jsp:include page="navbar.jsp"/>

    <div class="container py-4">
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 gap-3">
            <h2 class="mb-0"><i class="bi bi-grid"></i> Product Catalog</h2>
            <form action="${pageContext.request.contextPath}/products" method="get" class="d-flex" style="min-width:280px">
                <input type="text" name="search" class="form-control me-2" placeholder="Search products..."
                       value="${search}">
                <button type="submit" class="btn btn-primary"><i class="bi bi-search"></i></button>
            </form>
        </div>

        <c:if test="${not empty sessionScope.flashMessage}">
            <div class="alert alert-success">${sessionScope.flashMessage}</div>
            <c:remove var="flashMessage" scope="session"/>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <c:if test="${not empty product}">
            <div class="card shadow-sm mb-4 border-primary">
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0">${product.name}</h4>
                </div>
                <div class="card-body">
                    <p>${product.description}</p>
                    <p class="h4 text-primary">
                        <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="$"/>
                    </p>
                    <span class="badge bg-${product.quantity > 10 ? 'success' : product.quantity > 0 ? 'warning' : 'danger'} mb-3">
                        ${product.quantity} in stock
                    </span>
                    <c:if test="${product.quantity > 0}">
                        <form action="${pageContext.request.contextPath}/cart" method="post" class="d-inline">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="productId" value="${product.id}">
                            <div class="input-group" style="max-width:220px">
                                <input type="number" name="quantity" value="1" min="1" max="${product.quantity}" class="form-control">
                                <button type="submit" class="btn btn-primary"><i class="bi bi-cart-plus"></i> Add to Cart</button>
                            </div>
                        </form>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-secondary ms-2">Back to Catalog</a>
                </div>
            </div>
        </c:if>

        <div class="row g-4">
            <c:forEach var="product" items="${products}">
                <div class="col-md-4 col-lg-3">
                    <div class="card h-100 shadow-sm">
                        <div class="card-body d-flex flex-column">
                            <h5 class="card-title">
                                <a href="${pageContext.request.contextPath}/products?action=detail&id=${product.id}" class="text-decoration-none">
                                    ${product.name}
                                </a>
                            </h5>
                            <p class="card-text text-muted small flex-grow-1">${product.description}</p>
                            <div class="d-flex justify-content-between align-items-center mt-2">
                                <span class="h5 text-primary mb-0">
                                    <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="$"/>
                                </span>
                                <span class="badge bg-${product.quantity > 10 ? 'success' : product.quantity > 0 ? 'warning' : 'danger'}">
                                    ${product.quantity} in stock
                                </span>
                            </div>
                            <c:if test="${product.quantity > 0}">
                                <form action="${pageContext.request.contextPath}/cart" method="post" class="mt-3">
                                    <input type="hidden" name="action" value="add">
                                    <input type="hidden" name="productId" value="${product.id}">
                                    <div class="input-group">
                                        <input type="number" name="quantity" value="1" min="1" max="${product.quantity}" class="form-control">
                                        <button type="submit" class="btn btn-primary"><i class="bi bi-cart-plus"></i> Add</button>
                                    </div>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <c:if test="${empty products}">
            <div class="text-center py-5">
                <i class="bi bi-search display-1 text-muted"></i>
                <h4 class="mt-3">No products found</h4>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-primary mt-2">View All Products</a>
            </div>
        </c:if>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
