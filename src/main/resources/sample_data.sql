-- Sample data for Smart Items application
-- Run this after setting up the database

USE smartitemsdb;

-- Insert sample products
INSERT INTO products (name, category, brand, price, stock_quantity, description, image_path) VALUES
-- Smartphones
('iPhone 15 Pro', 'Smartphone', 'Apple', 999.99, 25, 'Latest iPhone with A17 Pro chip and titanium design', ''),
('Samsung Galaxy S24', 'Smartphone', 'Samsung', 899.99, 30, 'Flagship Android phone with AI features', ''),
('Redmi Note 13 Pro', 'Smartphone', 'Redmi', 299.99, 50, 'Budget-friendly smartphone with great camera', ''),
('OnePlus 12', 'Smartphone', 'OnePlus', 799.99, 20, 'Fast performance with Hasselblad camera', ''),

-- Tablets
('iPad Air', 'Tablet', 'Apple', 599.99, 15, 'Powerful tablet for work and creativity', ''),
('Samsung Galaxy Tab S9', 'Tablet', 'Samsung', 649.99, 12, 'Premium Android tablet with S Pen', ''),
('Xiaomi Pad 6', 'Tablet', 'Xiaomi', 349.99, 25, 'Affordable tablet with great performance', ''),

-- Laptops
('MacBook Pro 14"', 'Laptop', 'Apple', 1999.99, 10, 'Professional laptop with M3 chip', ''),
('Dell XPS 13', 'Laptop', 'Dell', 1299.99, 18, 'Ultrabook with InfinityEdge display', ''),
('ASUS ROG Strix', 'Laptop', 'ASUS', 1499.99, 8, 'Gaming laptop with RTX graphics', ''),
('Lenovo ThinkPad X1', 'Laptop', 'Lenovo', 1599.99, 12, 'Business laptop with premium build', ''),

-- Desktops
('iMac 24"', 'Desktop', 'Apple', 1299.99, 5, 'All-in-one desktop with M1 chip', ''),
('Dell OptiPlex', 'Desktop', 'Dell', 899.99, 15, 'Business desktop with Intel processor', ''),

-- Hardware Components
('NVIDIA RTX 4080', 'Hardware Components', 'NVIDIA', 1199.99, 8, 'High-end graphics card for gaming', ''),
('Intel Core i9-13900K', 'Hardware Components', 'Intel', 589.99, 20, 'High-performance processor', ''),
('Samsung 970 EVO SSD', 'Hardware Components', 'Samsung', 89.99, 100, 'Fast NVMe SSD storage', ''),

-- Smartwatches
('Apple Watch Series 9', 'Smartwatch', 'Apple', 399.99, 30, 'Advanced health monitoring smartwatch', ''),
('Samsung Galaxy Watch 6', 'Smartwatch', 'Samsung', 349.99, 25, 'Android smartwatch with health features', ''),

-- Accessories
('Apple AirPods Pro', 'Accessories', 'Apple', 249.99, 50, 'Wireless earbuds with noise cancellation', ''),
('Samsung Wireless Charger', 'Accessories', 'Samsung', 59.99, 75, 'Fast wireless charging pad', ''),
('USB-C Cable Pack', 'Accessories', 'Generic', 19.99, 200, 'High-quality USB-C cables', ''),

-- Gaming Consoles
('PlayStation 5', 'Gaming Console', 'Sony', 499.99, 15, 'Next-gen gaming console', ''),
('Xbox Series X', 'Gaming Console', 'Microsoft', 499.99, 12, 'Microsoft gaming console', ''),

-- Wearable Technology
('Oculus Quest 3', 'Wearable Technology', 'Meta', 499.99, 10, 'VR headset for immersive gaming', ''),

-- Networking Equipment
('TP-Link Archer C7', 'Networking Equipment', 'TP-Link', 79.99, 40, 'Dual-band wireless router', ''),
('Netgear Switch', 'Networking Equipment', 'Netgear', 129.99, 25, '8-port gigabit switch', ''),

-- Home Appliances
('Samsung Smart Fridge', 'Home Appliances', 'Samsung', 1299.99, 5, 'Smart refrigerator with touchscreen', ''),
('LG Washing Machine', 'Home Appliances', 'LG', 699.99, 8, 'Front-loading washing machine', '');

-- Insert sample customers
INSERT INTO customers (name, email, phone, address) VALUES
('John Smith', 'john.smith@email.com', '+1-555-0101', '123 Main St, New York, NY'),
('Sarah Johnson', 'sarah.j@email.com', '+1-555-0102', '456 Oak Ave, Los Angeles, CA'),
('Mike Davis', 'mike.davis@email.com', '+1-555-0103', '789 Pine Rd, Chicago, IL'),
('Emily Wilson', 'emily.w@email.com', '+1-555-0104', '321 Elm St, Houston, TX'),
('David Brown', 'david.brown@email.com', '+1-555-0105', '654 Maple Dr, Phoenix, AZ');

-- Insert sample employees
INSERT INTO employees (name, role, salary, status) VALUES
('Alex Chen', 'Sales Manager', 65000.00, 'active'),
('Lisa Rodriguez', 'Sales Representative', 45000.00, 'active'),
('Tom Anderson', 'Technician', 55000.00, 'active'),
('Maria Garcia', 'Customer Service', 40000.00, 'active'),
('James Wilson', 'Store Manager', 70000.00, 'active');

-- Insert sample orders
INSERT INTO orders (customer_id, order_date, status, total_amount) VALUES
(1, '2024-01-15', 'delivered', 999.99),
(2, '2024-01-16', 'processing', 1299.99),
(3, '2024-01-17', 'pending', 599.99),
(4, '2024-01-18', 'delivered', 399.99),
(5, '2024-01-19', 'shipped', 899.99);

-- Insert sample repairs
INSERT INTO repairs (customer_id, product_id, issue, status, assigned_to) VALUES
(1, 1, 'Screen cracked, needs replacement', 'in_progress', 3),
(2, 5, 'Battery not charging properly', 'pending', 3),
(3, 8, 'Keyboard not responding', 'completed', 3);

-- Insert sample salaries
INSERT INTO salaries (employee_id, salary_amount, bonus, deductions, payment_date) VALUES
(1, 65000.00, 5000.00, 2000.00, '2024-02-01'),
(2, 45000.00, 2000.00, 1500.00, '2024-02-01'),
(3, 55000.00, 3000.00, 1800.00, '2024-02-01'),
(4, 40000.00, 1500.00, 1200.00, '2024-02-01'),
(5, 70000.00, 8000.00, 2500.00, '2024-02-01');

SELECT 'Sample data inserted successfully!' as status;





