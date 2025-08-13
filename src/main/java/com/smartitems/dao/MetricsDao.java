package com.smartitems.dao;

import com.smartitems.config.Database;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class MetricsDao {
    
    public int getTotalStockQuantity() throws SQLException {
        String sql = "SELECT COALESCE(SUM(stock_quantity), 0) FROM products";
        try (Connection c = Database.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
    
    public int getTotalOrders() throws SQLException {
        String sql = "SELECT COUNT(*) FROM orders";
        try (Connection c = Database.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
    
    public int getPendingOrders() throws SQLException {
        String sql = "SELECT COUNT(*) FROM orders WHERE status = 'pending'";
        try (Connection c = Database.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
    
    public int getCompletedOrders() throws SQLException {
        String sql = "SELECT COUNT(*) FROM orders WHERE status = 'delivered'";
        try (Connection c = Database.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
    
    public BigDecimal getTotalEarnings() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status = 'delivered'";
        try (Connection c = Database.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }
    
    public BigDecimal getPendingAmount() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status IN ('pending', 'processing')";
        try (Connection c = Database.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }
    
    public int getTotalCustomers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers";
        try (Connection c = Database.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
    
    public int getOngoingRepairs() throws SQLException {
        String sql = "SELECT COUNT(*) FROM repairs WHERE status IN ('pending', 'in_progress')";
        try (Connection c = Database.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
    
    public BigDecimal getTotalSalariesDue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(salary_amount + bonus - deductions), 0) FROM salaries WHERE payment_date <= ?";
        try (Connection c = Database.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
                return BigDecimal.ZERO;
            }
        }
    }
    
    public int getSalariesDueToday() throws SQLException {
        String sql = "SELECT COUNT(*) FROM salaries WHERE payment_date = ?";
        try (Connection c = Database.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }
    
    public int getLowStockProducts() throws SQLException {
        String sql = "SELECT COUNT(*) FROM products WHERE stock_quantity <= 10";
        try (Connection c = Database.getConnection(); 
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
}

