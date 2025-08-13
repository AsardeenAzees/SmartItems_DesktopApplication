package com.smartitems.dao;

import com.smartitems.config.Database;
import com.smartitems.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {
    public void createTableIfNotExists() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS products (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(150) NOT NULL,
                    category VARCHAR(100) NOT NULL,
                    brand VARCHAR(100) NOT NULL,
                    price DECIMAL(10,2) NOT NULL,
                    stock_quantity INT NOT NULL,
                    description TEXT,
                    image_path VARCHAR(500),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """;
        try (Connection c = Database.getConnection(); Statement st = c.createStatement()) {
            st.execute(sql);
            
            // Check if image_path column exists, if not add it
            try {
                st.execute("SELECT image_path FROM products LIMIT 1");
            } catch (SQLException e) {
                // Column doesn't exist, add it
                st.execute("ALTER TABLE products ADD COLUMN image_path VARCHAR(500) AFTER description");
            }
            
            // Check if brand column exists, if not add it
            try {
                st.execute("SELECT brand FROM products LIMIT 1");
            } catch (SQLException e) {
                // Column doesn't exist, add it
                st.execute("ALTER TABLE products ADD COLUMN brand VARCHAR(100) AFTER category");
            }
        }
    }

    public void insert(Product p) throws SQLException {
        // First ensure the table and column exist
        createTableIfNotExists();
        
        String sql = "INSERT INTO products(name,category,brand,price,stock_quantity,description,image_path) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setString(3, p.getBrand());
            ps.setBigDecimal(4, p.getPrice());
            ps.setInt(5, p.getStockQuantity());
            ps.setString(6, p.getDescription());
            ps.setString(7, p.getImagePath());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getLong(1));
            }
        }
    }

    public List<Product> search(String keyword) throws SQLException {
        // First ensure the table and column exist
        createTableIfNotExists();
        
        String sql = "SELECT id,name,category,brand,price,stock_quantity,description,image_path,created_at FROM products WHERE name LIKE ? OR category LIKE ? OR brand LIKE ? ORDER BY id DESC";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            String q = "%" + keyword + "%";
            ps.setString(1, q);
            ps.setString(2, q);
            ps.setString(3, q);
            try (ResultSet rs = ps.executeQuery()) {
                List<Product> list = new ArrayList<>();
                while (rs.next()) {
                    Product p = new Product();
                    p.setId(rs.getLong("id"));
                    p.setName(rs.getString("name"));
                    p.setCategory(rs.getString("category"));
                    p.setBrand(rs.getString("brand"));
                    p.setPrice(rs.getBigDecimal("price"));
                    p.setStockQuantity(rs.getInt("stock_quantity"));
                    p.setDescription(rs.getString("description"));
                    p.setImagePath(rs.getString("image_path"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) p.setCreatedAt(ts.toLocalDateTime());
                    list.add(p);
                }
                return list;
            }
        }
    }

    public void update(Product p) throws SQLException {
        String sql = "UPDATE products SET name=?, category=?, brand=?, price=?, stock_quantity=?, description=?, image_path=? WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setString(3, p.getBrand());
            ps.setBigDecimal(4, p.getPrice());
            ps.setInt(5, p.getStockQuantity());
            ps.setString(6, p.getDescription());
            ps.setString(7, p.getImagePath());
            ps.setLong(8, p.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM products WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
    
    public void updateStock(long productId, int quantity) throws SQLException {
        String sql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ? AND stock_quantity >= ?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setLong(2, productId);
            ps.setInt(3, quantity);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Insufficient stock for product ID: " + productId);
            }
        }
    }
    
    public Product findById(long id) throws SQLException {
        String sql = "SELECT id,name,category,brand,price,stock_quantity,description,image_path,created_at FROM products WHERE id = ?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Product p = new Product();
                p.setId(rs.getLong("id"));
                p.setName(rs.getString("name"));
                p.setCategory(rs.getString("category"));
                p.setBrand(rs.getString("brand"));
                p.setPrice(rs.getBigDecimal("price"));
                p.setStockQuantity(rs.getInt("stock_quantity"));
                p.setDescription(rs.getString("description"));
                p.setImagePath(rs.getString("image_path"));
                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) p.setCreatedAt(ts.toLocalDateTime());
                return p;
            }
        }
    }
}



