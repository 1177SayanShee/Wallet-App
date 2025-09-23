//package com.walletapp.util;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class DBUtil {
//    private static final String URL = "jdbc:mysql://localhost:3306/wallet_app?useSSL=false&serverTimezone=UTC";
//    private static final String USER = "root";       // <-- set your DB username
//    private static final String PASS = "91636Ab*#";   // <-- set your DB password
//
//    static {
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(URL, USER, PASS);
//    }
//}


package com.walletapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DBUtil {

    // Singleton instance
    private static volatile DBUtil instance;

    // Database properties
    private String driver;
    private String url;
    private String user;
    private String password;

    // Private constructor to prevent instantiation
    private DBUtil() {
        loadProperties();  // load from db.properties
        loadDriver();      // load driver dynamically
    }

    // Load DB properties from db.properties file
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

    // Load JDBC driver
    private void loadDriver() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading JDBC driver: " + driver, e);
        }
    }

    // Public static method to provide access to the single instance (Double-checked locking)
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

    // Get a new DB connection
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
