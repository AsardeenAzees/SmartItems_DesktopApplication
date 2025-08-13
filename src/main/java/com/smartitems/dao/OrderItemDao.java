package com.smartitems.dao;

import com.smartitems.config.Database;
import com.smartitems.model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDao {
    
    public void createTableIfNotExists() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS order_items (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    order_id BIGINT NOT NULL,
                    product_id BIGINT NOT NULL,
                    quantity INT NOT NULL,
                    price DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                    FOREIGN KEY (product_id) REFERENCES products(id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """;
        try (Connection c = Database.getConnection(); Statement st = c.createStatement()) {
            st.execute(sql);
        }
    }
    
    public void insert(OrderItem item) throws SQLException {
        String sql = "INSERT INTO order_items(order_id, product_id, quantity, price) VALUES (?,?,?,?)";
        try (Connection c = Database.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, item.getOrderId());
            ps.setLong(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            ps.setBigDecimal(4, item.getPrice());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) item.setId(rs.getLong(1));
            }
        }
    }
    
    public List<OrderItem> findByOrderId(long orderId) throws SQLException {
        String sql = """
                SELECT oi.id, oi.order_id, oi.product_id, oi.quantity, oi.price, p.name as product_name
                FROM order_items oi
                JOIN products p ON oi.product_id = p.id
                WHERE oi.order_id = ?
                ORDER BY oi.id
                """;
        try (Connection c = Database.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                List<OrderItem> items = new ArrayList<>();
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getLong("id"));
                    item.setOrderId(rs.getLong("order_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPrice(rs.getBigDecimal("price"));
                    item.setProductName(rs.getString("product_name"));
                    items.add(item);
                }
                return items;
            }
        }
    }
    
    public void deleteByOrderId(long orderId) throws SQLException {
        String sql = "DELETE FROM order_items WHERE order_id = ?";
        try (Connection c = Database.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.executeUpdate();
        }
    }
    
    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM order_items WHERE id = ?";
        try (Connection c = Database.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}

