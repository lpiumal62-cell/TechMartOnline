<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TechMart Online - Enterprise E-Commerce</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <style>
        .hero { background: linear-gradient(135deg, #1a237e 0%, #283593 50%, #3949ab 100%); color: white; padding: 80px 0; }
        .feature-icon { font-size: 2.5rem; color: #3949ab; }
        .card-hover:hover { transform: translateY(-5px); transition: transform 0.3s; box-shadow: 0 8px 25px rgba(0,0,0,0.15); }
    </style>
</head>
<body>
    <jsp:include page="jsp/navbar.jsp"/>

    <section class="hero text-center">
        <div class="container">
            <h1 class="display-4 fw-bold mb-3">Welcome to TechMart Online</h1>
            <p class="lead mb-4">Enterprise e-commerce powered by Jakarta EE — EJB, JMS, JPA &amp; CDI</p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-light btn-lg me-2">
                <i class="bi bi-grid"></i> Browse Products
            </a>
            <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-light btn-lg me-2">
                <i class="bi bi-cart3"></i> View Cart
            </a>
            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-light btn-lg">
                <i class="bi bi-speedometer2"></i> Dashboard
            </a>
        </div>
    </section>

    <section class="py-5">
        <div class="container">
            <h2 class="text-center mb-5">Enterprise Features</h2>
            <div class="row g-4">
                <div class="col-md-4">
                    <div class="card h-100 card-hover border-0 shadow-sm text-center p-4">
                        <i class="bi bi-lightning-charge feature-icon mb-3"></i>
                        <h5>Sub-Second Response</h5>
                        <p class="text-muted">Stateless EJB pooling and inventory caching deliver fast responses for 10,000+ concurrent users.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card h-100 card-hover border-0 shadow-sm text-center p-4">
                        <i class="bi bi-arrow-repeat feature-icon mb-3"></i>
                        <h5>Real-Time Inventory</h5>
                        <p class="text-muted">Singleton InventoryManager with JMS topic broadcasting ensures synchronized stock levels.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card h-100 card-hover border-0 shadow-sm text-center p-4">
                        <i class="bi bi-envelope feature-icon mb-3"></i>
                        <h5>Async Notifications</h5>
                        <p class="text-muted">@Asynchronous order processing and MDB-driven fulfillment with automated email simulation.</p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <footer class="bg-dark text-white text-center py-4">
        <p class="mb-0">&copy; 2026 TechMart Online | Jakarta EE Enterprise Application</p>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
