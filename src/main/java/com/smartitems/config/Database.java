package com.smartitems.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class Database {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/smartitemsdb?useSSL=false&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private Database() {}

    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
        
        String url = System.getenv().getOrDefault("SMART_DB_URL", DEFAULT_URL);
        String user = System.getenv().getOrDefault("SMART_DB_USER", DEFAULT_USER);
        String pass = System.getenv().getOrDefault("SMART_DB_PASSWORD", DEFAULT_PASSWORD);

        Properties props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", pass);
        props.setProperty("autoReconnect", "true");
        props.setProperty("useUnicode", "true");
        props.setProperty("characterEncoding", "UTF-8");
        props.setProperty("allowPublicKeyRetrieval", "true");
        props.setProperty("useSSL", "false");
        return DriverManager.getConnection(url, props);
    }
}



