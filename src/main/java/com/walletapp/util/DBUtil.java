package com.walletapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Utility class for managing database connections.
 * Implements a thread-safe Singleton pattern to ensure a single instance.
 * Loads database configuration from the <code>db.properties</code> file
 * and dynamically loads the JDBC driver.
 */
public class DBUtil {

    /** Singleton instance of DBUtil */
    private static volatile DBUtil instance;

    /** Fully qualified JDBC driver class name */
    private String driver;

    /** JDBC connection URL */
    private String url;

    /** Database username */
    private String user;

    /** Database password */
    private String password;

    /**
     * Private constructor to prevent external instantiation.
     * Loads database properties and the JDBC driver.
     */
    private DBUtil() {
        loadProperties();
        loadDriver();
    }

    /**
     * Loads database connection properties from <code>db.properties</code> file.
     * @throws RuntimeException if the properties file cannot be found or loaded
     */
    private void loadProperties() {
        Properties props = new Properties();
        try (InputStream input = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find db.properties");
            }
            props.load(input);
            this.driver = props.getProperty("db.driver");
            this.url = props.getProperty("db.url");
            this.user = props.getProperty("db.user");
            this.password = props.getProperty("db.password");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading db.properties", e);
        }
    }

    /**
     * Dynamically loads the JDBC driver class.
     * @throws RuntimeException if the driver class cannot be found
     */
    private void loadDriver() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading JDBC driver: " + driver, e);
        }
    }

    /**
     * Provides access to the singleton instance of DBUtil.
     * Implements double-checked locking for thread-safety.
     * @return singleton instance of DBUtil
     */
    public static DBUtil getInstance() {
        if (instance == null) {
            synchronized (DBUtil.class) {
                if (instance == null) {
                    instance = new DBUtil();
                }
            }
        }
        return instance;
    }

    /**
     * Creates and returns a new database connection.
     * @return a new JDBC Connection
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
