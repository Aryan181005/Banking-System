// src/util/DatabaseConnection.java
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/banking_system?autoReconnect=true&useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "aryan@23"; // Replace with your MySQL password
    
    private static Connection connection = null;
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database connected successfully!");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
            // Try to reconnect
            connection = reconnect();
        }
        return connection;
    }
    
    private static Connection reconnect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection newConnection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database reconnected successfully!");
            return newConnection;
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Reconnection failed: " + e.getMessage());
            return null;
        }
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Test connection method
    public static boolean testConnection() {
        try (Connection testConn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            return true;
        } catch (SQLException e) {
            System.out.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}