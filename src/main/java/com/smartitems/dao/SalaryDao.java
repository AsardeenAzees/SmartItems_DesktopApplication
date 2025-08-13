package com.smartitems.dao;

import com.smartitems.config.Database;
import com.smartitems.model.Salary;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SalaryDao {
    public void createTableIfNotExists() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS salaries (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    employee_id BIGINT NOT NULL,
                    salary_amount DECIMAL(10,2) NOT NULL,
                    bonus DECIMAL(10,2) DEFAULT 0,
                    deductions DECIMAL(10,2) DEFAULT 0,
                    payment_date DATE NOT NULL,
                    FOREIGN KEY (employee_id) REFERENCES employees(id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """;
        try (Connection c = Database.getConnection(); Statement st = c.createStatement()) {
            st.execute(sql);
        }
    }

    public void insert(Salary salary) throws SQLException {
        String sql = "INSERT INTO salaries(employee_id, salary_amount, bonus, deductions, payment_date) VALUES (?,?,?,?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, salary.getEmployeeId());
            ps.setBigDecimal(2, salary.getSalaryAmount());
            ps.setBigDecimal(3, salary.getBonus() != null ? salary.getBonus() : java.math.BigDecimal.ZERO);
            ps.setBigDecimal(4, salary.getDeductions() != null ? salary.getDeductions() : java.math.BigDecimal.ZERO);
            ps.setDate(5, salary.getPaymentDate() != null ? Date.valueOf(salary.getPaymentDate()) : Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) salary.setId(rs.getLong(1));
            }
        }
    }

    public List<Salary> search(String keyword) throws SQLException {
        String sql = """
                SELECT s.id, s.employee_id, s.salary_amount, s.bonus, s.deductions, s.payment_date, e.name as employee_name
                FROM salaries s
                JOIN employees e ON s.employee_id = e.id
                WHERE e.name LIKE ? OR CAST(s.id AS CHAR) LIKE ?
                ORDER BY s.id DESC
                """;
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            String q = "%" + keyword + "%";
            ps.setString(1, q);
            ps.setString(2, q);
            try (ResultSet rs = ps.executeQuery()) {
                List<Salary> list = new ArrayList<>();
                while (rs.next()) {
                    Salary s = new Salary();
                    s.setId(rs.getLong("id"));
                    s.setEmployeeId(rs.getLong("employee_id"));
                    s.setSalaryAmount(rs.getBigDecimal("salary_amount"));
                    s.setBonus(rs.getBigDecimal("bonus"));
                    s.setDeductions(rs.getBigDecimal("deductions"));
                    Date paymentDate = rs.getDate("payment_date");
                    if (paymentDate != null) s.setPaymentDate(paymentDate.toLocalDate());
                    s.setEmployeeName(rs.getString("employee_name"));
                    list.add(s);
                }
                return list;
            }
        }
    }

    public void update(Salary salary) throws SQLException {
        String sql = "UPDATE salaries SET employee_id=?, salary_amount=?, bonus=?, deductions=?, payment_date=? WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, salary.getEmployeeId());
            ps.setBigDecimal(2, salary.getSalaryAmount());
            ps.setBigDecimal(3, salary.getBonus());
            ps.setBigDecimal(4, salary.getDeductions());
            ps.setDate(5, salary.getPaymentDate() != null ? Date.valueOf(salary.getPaymentDate()) : Date.valueOf(LocalDate.now()));
            ps.setLong(6, salary.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM salaries WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
}



