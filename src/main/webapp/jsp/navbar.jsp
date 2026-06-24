<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">
            <i class="bi bi-shop"></i> TechMart Online
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/">Home</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/products">Products</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/cart"><i class="bi bi-cart3"></i> Cart</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/checkout">Checkout</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/dashboard"><i class="bi bi-speedometer2"></i> Dashboard</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/performance"><i class="bi bi-graph-up"></i> Performance</a></li>
            </ul>
        </div>
    </div>
</nav>
