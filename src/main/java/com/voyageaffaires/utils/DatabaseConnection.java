package com.voyageaffaires.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages database connections for the application.
 * Implements singleton pattern to ensure single connection instance.
 */
public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private Connection connection;
    private String url;
    private String username;
    private String password;
    
    /**
     * Private constructor to prevent instantiation.
     * Loads database configuration from properties file.
     */
    private DatabaseConnection() {
        loadDatabaseConfig();
    }
    
    /**
     * Gets the singleton instance of DatabaseConnection.
     * 
     * @return DatabaseConnection instance
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
    
    /**
     * Loads database configuration from properties file.
     */
    private void loadDatabaseConfig() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {
            
            if (input == null) {
                System.err.println("Unable to find database.properties");
                // Default values for local development
                this.url = "jdbc:mysql://localhost:3306/voyage_affaires?useSSL=false&serverTimezone=UTC";
                this.username = "root";
                this.password = "";
                return;
            }
            
            props.load(input);
            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");
            
            // Load MySQL driver
            Class.forName(props.getProperty("db.driver"));
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Gets an active database connection.
     * Creates a new connection if one doesn't exist or is closed.
     * 
     * @return Active database connection
     * @throws SQLException if connection cannot be established
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Database connection established successfully.");
            } catch (SQLException e) {
                System.err.println("Failed to create database connection: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
    
    /**
     * Tests the database connection.
     * 
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}
