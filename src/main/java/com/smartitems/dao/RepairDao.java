package com.smartitems.dao;

import com.smartitems.config.Database;
import com.smartitems.model.Repair;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepairDao {
    public void createTableIfNotExists() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS repairs (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    customer_id BIGINT NOT NULL,
                    product_id BIGINT,
                    issue VARCHAR(255) NOT NULL,
                    status VARCHAR(50) DEFAULT 'pending',
                    assigned_to BIGINT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (customer_id) REFERENCES customers(id),
                    FOREIGN KEY (product_id) REFERENCES products(id),
                    FOREIGN KEY (assigned_to) REFERENCES employees(id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """;
        try (Connection c = Database.getConnection(); Statement st = c.createStatement()) {
            st.execute(sql);
        }
    }

    public void insert(Repair repair) throws SQLException {
        String sql = "INSERT INTO repairs(customer_id, product_id, issue, status, assigned_to) VALUES (?,?,?,?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, repair.getCustomerId());
            ps.setObject(2, repair.getProductId());
            ps.setString(3, repair.getIssue());
            ps.setString(4, repair.getStatus() != null ? repair.getStatus() : "pending");
            ps.setObject(5, repair.getAssignedTo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) repair.setId(rs.getLong(1));
            }
        }
    }

    public List<Repair> search(String keyword) throws SQLException {
        String sql = """
                SELECT r.id, r.customer_id, r.product_id, r.issue, r.status, r.assigned_to, r.created_at,
                       c.name as customer_name, p.name as product_name, e.name as assigned_to_name
                FROM repairs r
                JOIN customers c ON r.customer_id = c.id
                LEFT JOIN products p ON r.product_id = p.id
                LEFT JOIN employees e ON r.assigned_to = e.id
                WHERE r.status LIKE ? OR c.name LIKE ? OR p.name LIKE ? OR r.issue LIKE ?
                ORDER BY r.id DESC
                """;
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            String q = "%" + keyword + "%";
            ps.setString(1, q);
            ps.setString(2, q);
            ps.setString(3, q);
            ps.setString(4, q);
            try (ResultSet rs = ps.executeQuery()) {
                List<Repair> list = new ArrayList<>();
                while (rs.next()) {
                    Repair r = new Repair();
                    r.setId(rs.getLong("id"));
                    r.setCustomerId(rs.getLong("customer_id"));
                    r.setProductId(rs.getObject("product_id", Long.class));
                    r.setIssue(rs.getString("issue"));
                    r.setStatus(rs.getString("status"));
                    r.setAssignedTo(rs.getObject("assigned_to", Long.class));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) r.setCreatedAt(ts.toLocalDateTime());
                    r.setCustomerName(rs.getString("customer_name"));
                    r.setProductName(rs.getString("product_name"));
                    r.setAssignedToName(rs.getString("assigned_to_name"));
                    list.add(r);
                }
                return list;
            }
        }
    }

    public void update(Repair repair) throws SQLException {
        String sql = "UPDATE repairs SET customer_id=?, product_id=?, issue=?, status=?, assigned_to=? WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, repair.getCustomerId());
            ps.setObject(2, repair.getProductId());
            ps.setString(3, repair.getIssue());
            ps.setString(4, repair.getStatus());
            ps.setObject(5, repair.getAssignedTo());
            ps.setLong(6, repair.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM repairs WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}



