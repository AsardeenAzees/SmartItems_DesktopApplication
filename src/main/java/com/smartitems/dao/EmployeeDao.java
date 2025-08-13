package com.smartitems.dao;

import com.smartitems.config.Database;
import com.smartitems.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDao {
    public void createTableIfNotExists() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS employees (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(150) NOT NULL,
                    role VARCHAR(100),
                    salary DECIMAL(10,2),
                    status VARCHAR(20) DEFAULT 'active'
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """;
        try (Connection c = Database.getConnection(); Statement st = c.createStatement()) {
            st.execute(sql);
        }
    }

    public void insert(Employee emp) throws SQLException {
        String sql = "INSERT INTO employees(name,role,salary,status) VALUES (?,?,?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getRole());
            ps.setBigDecimal(3, emp.getSalary());
            ps.setString(4, emp.getStatus() != null ? emp.getStatus() : "active");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) emp.setId(rs.getLong(1));
            }
        }
    }

    public List<Employee> search(String keyword) throws SQLException {
        String sql = "SELECT id,name,role,salary,status FROM employees WHERE name LIKE ? OR role LIKE ? ORDER BY id DESC";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            String q = "%" + keyword + "%";
            ps.setString(1, q);
            ps.setString(2, q);
            try (ResultSet rs = ps.executeQuery()) {
                List<Employee> list = new ArrayList<>();
                while (rs.next()) {
                    Employee emp = new Employee();
                    emp.setId(rs.getLong("id"));
                    emp.setName(rs.getString("name"));
                    emp.setRole(rs.getString("role"));
                    emp.setSalary(rs.getBigDecimal("salary"));
                    emp.setStatus(rs.getString("status"));
                    list.add(emp);
                }
                return list;
            }
        }
    }

    public void update(Employee emp) throws SQLException {
        String sql = "UPDATE employees SET name=?, role=?, salary=?, status=? WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getRole());
            ps.setBigDecimal(3, emp.getSalary());
            ps.setString(4, emp.getStatus());
            ps.setLong(5, emp.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}



