package com.sharespace.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton database connection manager using JDBC DriverManager.
 * Provides a connection to the MySQL ShareSpace database.
 * Supports auto-fallback and saving user-provided passwords.
 */
public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/sharespace?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static String password = "root";

    private static final String CONFIG_FILE = "db.properties";
    private static Connection connection;

    static {
        loadSavedPassword();
    }

    private DBConnection() {}

    private static void loadSavedPassword() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                Properties props = new Properties();
                props.load(in);
                String saved = props.getProperty("db.password");
                if (saved != null) {
                    password = saved;
                }
            } catch (IOException ignored) {}
        }
    }

    public static void setPassword(String newPassword) {
        password = newPassword;
        closeConnection();
        // Save to db.properties for future runs
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            Properties props = new Properties();
            props.setProperty("db.password", newPassword);
            props.store(out, "ShareSpace Database Configuration");
        } catch (IOException ignored) {}
    }

    public static String getPassword() {
        return password;
    }

    /**
     * Returns a singleton Connection object.
     * Creates a new connection if none exists or if the current one is closed.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, password);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-j to classpath.", e);
            }
        }
        return connection;
    }

    /**
     * Closes the current connection if open.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ignored) {
            } finally {
                connection = null;
            }
        }
    }

    /**
     * Tests if the database is reachable with the current password.
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
