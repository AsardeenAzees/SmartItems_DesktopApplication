package com.smartitems.dao;

import com.smartitems.config.Database;
import com.smartitems.model.Order;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {
    public void createTableIfNotExists() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS orders (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    customer_id BIGINT NOT NULL,
                    order_date DATE NOT NULL,
                    status VARCHAR(50) DEFAULT 'pending',
                    total_amount DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (customer_id) REFERENCES customers(id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """;
        try (Connection c = Database.getConnection(); Statement st = c.createStatement()) {
            st.execute(sql);
        }
    }

    public void insert(Order order) throws SQLException {
        String sql = "INSERT INTO orders(customer_id, order_date, status, total_amount) VALUES (?,?,?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, order.getCustomerId());
            ps.setDate(2, order.getOrderDate() != null ? Date.valueOf(order.getOrderDate()) : Date.valueOf(LocalDate.now()));
            ps.setString(3, order.getStatus() != null ? order.getStatus() : "pending");
            ps.setBigDecimal(4, order.getTotalAmount());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) order.setId(rs.getLong(1));
            }
        }
    }

    public List<Order> search(String keyword) throws SQLException {
        String sql = """
                SELECT o.id, o.customer_id, o.order_date, o.status, o.total_amount, c.name as customer_name
                FROM orders o
                JOIN customers c ON o.customer_id = c.id
                WHERE o.status LIKE ? OR c.name LIKE ? OR CAST(o.id AS CHAR) LIKE ?
                ORDER BY o.id DESC
                """;
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            String q = "%" + keyword + "%";
            ps.setString(1, q);
            ps.setString(2, q);
            ps.setString(3, q);
            try (ResultSet rs = ps.executeQuery()) {
                List<Order> list = new ArrayList<>();
                while (rs.next()) {
                    Order o = new Order();
                    o.setId(rs.getLong("id"));
                    o.setCustomerId(rs.getLong("customer_id"));
                    Date orderDate = rs.getDate("order_date");
                    if (orderDate != null) o.setOrderDate(orderDate.toLocalDate());
                    o.setStatus(rs.getString("status"));
                    o.setTotalAmount(rs.getBigDecimal("total_amount"));
                    o.setCustomerName(rs.getString("customer_name"));
                    list.add(o);
                }
                return list;
            }
        }
    }

    public void update(Order order) throws SQLException {
        String sql = "UPDATE orders SET customer_id=?, order_date=?, status=?, total_amount=? WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, order.getCustomerId());
            ps.setDate(2, order.getOrderDate() != null ? Date.valueOf(order.getOrderDate()) : Date.valueOf(LocalDate.now()));
            ps.setString(3, order.getStatus());
            ps.setBigDecimal(4, order.getTotalAmount());
            ps.setLong(5, order.getId());
            ps.executeUpdate();
        }
    }
    
    public void updateOrderStatus(long orderId, String newStatus) throws SQLException {
        String sql = "UPDATE orders SET status=? WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setLong(2, orderId);
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM orders WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}



