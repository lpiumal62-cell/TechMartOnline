-- TechMart Online Database Schema
-- MySQL 8.0+
-- Run: mysql -u root -p < techmart.sql

CREATE DATABASE IF NOT EXISTS techmart_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'techmart'@'localhost' IDENTIFIED BY 'techmart_pass';
GRANT ALL PRIVILEGES ON techmart_db.* TO 'techmart'@'localhost';
FLUSH PRIVILEGES;

USE techmart_db;

-- Products table
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS products;

CREATE TABLE products (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(200)    NOT NULL,
    description VARCHAR(1000),
    price       DECIMAL(10, 2)  NOT NULL,
    quantity    INT             NOT NULL DEFAULT 0,
    created_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_products_name (name),
    INDEX idx_products_quantity (quantity)
) ENGINE=InnoDB;

-- Customers table
CREATE TABLE customers (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(150)    NOT NULL,
    email       VARCHAR(200)    NOT NULL UNIQUE,
    password    VARCHAR(255)    NOT NULL,
    created_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_customers_email (email)
) ENGINE=InnoDB;

-- Orders table
CREATE TABLE orders (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id     BIGINT          NOT NULL,
    total_amount    DECIMAL(12, 2)  NOT NULL,
    order_date      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    INDEX idx_orders_customer (customer_id),
    INDEX idx_orders_status (status),
    INDEX idx_orders_date (order_date)
) ENGINE=InnoDB;

-- Order items table
CREATE TABLE order_items (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id    BIGINT          NOT NULL,
    product_id  BIGINT          NOT NULL,
    quantity    INT             NOT NULL,
    price       DECIMAL(10, 2)  NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_order_items_order (order_id),
    INDEX idx_order_items_product (product_id)
) ENGINE=InnoDB;

-- Sample Products
INSERT INTO products (name, description, price, quantity) VALUES
('MacBook Pro 16"', 'Apple M3 Pro chip, 18GB RAM, 512GB SSD', 2499.99, 50),
('Dell XPS 15', 'Intel Core i7, 32GB RAM, 1TB SSD, OLED Display', 1899.99, 35),
('Samsung Galaxy S24 Ultra', '256GB, Titanium Gray, S Pen included', 1299.99, 100),
('Sony WH-1000XM5', 'Wireless Noise Cancelling Headphones', 349.99, 200),
('Logitech MX Master 3S', 'Wireless Performance Mouse', 99.99, 500),
('Keychron K2 Pro', 'Mechanical Keyboard, Hot-swappable', 89.99, 150),
('LG UltraFine 27"', '4K IPS Monitor, USB-C', 699.99, 75),
('iPad Pro 12.9"', 'M2 chip, 256GB, Wi-Fi', 1099.99, 60),
('Anker PowerCore 26800', '26800mAh Portable Charger', 65.99, 300),
('Razer DeathAdder V3', 'Gaming Mouse, 30000 DPI', 69.99, 250);

-- Sample Customers (passwords are plain text for demo - use hashing in production)
INSERT INTO customers (name, email, password) VALUES
('John Smith', 'john.smith@email.com', 'password123'),
('Jane Doe', 'jane.doe@email.com', 'securepass'),
('Admin User', 'admin@techmart.com', 'admin123'),
('Alice Johnson', 'alice.j@email.com', 'alice2026'),
('Bob Wilson', 'bob.wilson@email.com', 'bobpass');

-- Sample Orders
INSERT INTO orders (customer_id, total_amount, order_date, status) VALUES
(1, 2499.99, '2026-01-15 10:30:00', 'DELIVERED'),
(2, 1649.98, '2026-02-20 14:45:00', 'SHIPPED'),
(1, 449.98, '2026-03-10 09:15:00', 'PROCESSING'),
(3, 3799.97, '2026-04-05 16:00:00', 'PENDING'),
(4, 1299.99, '2026-05-18 11:20:00', 'DELIVERED');

-- Sample Order Items
INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(1, 1, 1, 2499.99),
(2, 4, 1, 349.99),
(2, 5, 1, 99.99),
(2, 6, 1, 89.99),
(2, 10, 1, 69.99),
(2, 9, 1, 65.99),
(2, 5, 1, 99.99),
(3, 4, 1, 349.99),
(3, 5, 1, 99.99),
(4, 1, 1, 2499.99),
(4, 2, 1, 1899.99),
(5, 3, 1, 1299.99);

-- Verify data
SELECT 'Products' AS table_name, COUNT(*) AS row_count FROM products
UNION ALL SELECT 'Customers', COUNT(*) FROM customers
UNION ALL SELECT 'Orders', COUNT(*) FROM orders
UNION ALL SELECT 'Order Items', COUNT(*) FROM order_items;
