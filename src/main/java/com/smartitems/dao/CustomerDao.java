package com.smartitems.dao;

import com.smartitems.config.Database;
import com.smartitems.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDao {
    public void createTableIfNotExists() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS customers (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(150) NOT NULL,
                    email VARCHAR(150),
                    phone VARCHAR(50),
                    address VARCHAR(255)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """;
        try (Connection c = Database.getConnection(); Statement st = c.createStatement()) {
            st.execute(sql);
        }
    }

    public void insert(Customer cst) throws SQLException {
        String sql = "INSERT INTO customers(name,email,phone,address) VALUES (?,?,?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cst.getName());
            ps.setString(2, cst.getEmail());
            ps.setString(3, cst.getPhone());
            ps.setString(4, cst.getAddress());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) cst.setId(rs.getLong(1));
            }
        }
    }

    public void update(Customer cst) throws SQLException {
        String sql = "UPDATE customers SET name=?, email=?, phone=?, address=? WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cst.getName());
            ps.setString(2, cst.getEmail());
            ps.setString(3, cst.getPhone());
            ps.setString(4, cst.getAddress());
            ps.setLong(5, cst.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM customers WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public List<Customer> search(String keyword) throws SQLException {
        String sql = "SELECT id,name,email,phone,address FROM customers WHERE name LIKE ? OR email LIKE ? OR phone LIKE ? ORDER BY id DESC";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            String q = "%" + keyword + "%";
            ps.setString(1, q);
            ps.setString(2, q);
            ps.setString(3, q);
            try (ResultSet rs = ps.executeQuery()) {
                List<Customer> list = new ArrayList<>();
                while (rs.next()) {
                    Customer cst = new Customer();
                    cst.setId(rs.getLong("id"));
                    cst.setName(rs.getString("name"));
                    cst.setEmail(rs.getString("email"));
                    cst.setPhone(rs.getString("phone"));
                    cst.setAddress(rs.getString("address"));
                    list.add(cst);
                }
                return list;
            }
        }
    }
}



