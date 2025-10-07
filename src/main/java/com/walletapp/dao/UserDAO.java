package com.walletapp.dao;

import com.walletapp.model.User;
import com.walletapp.util.DBUtil;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Date;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


/**
 * Data Access Object (DAO) for performing operations on {@link User} entities.
 * Provides methods for user creation, password management, and temporary password handling.
 */
public class UserDAO {

	
	
	 /**
     * Finds a user by their email address.
     *
     * @param email the email of the user to search for.
     * @return an {@link Optional} containing the {@link User} if found, or empty if not.
     */
    public static Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection con = DBUtil.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("user_id"));
                u.setEmail(rs.getString("email"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setTempPasswordHash(rs.getString("temp_password_hash"));
                Timestamp t = rs.getTimestamp("temp_password_expiry");
                if (t != null) u.setTempPasswordExpiry(new Date(t.getTime()));
                u.setMustChangePassword(rs.getBoolean("must_change_password"));
                return Optional.of(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    
    /**
     * Creates a new user with a hashed password.
     *
     * @param email the email of the new user.
     * @param plainPassword the plain text password to hash and store.
     * @return true if the user was successfully created, false otherwise.
     */
    public static boolean createUser(String email, String plainPassword) {
        String sql = "INSERT INTO users (email, password_hash, must_change_password) VALUES (?, ?, ?)";
        String hash = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
        try (Connection con = DBUtil.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, hash);
            ps.setBoolean(3, false);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    
    /**
     * Generates a temporary password for a user, stores its hash and expiry in the database,
     * and forces the user to change it on next login.
     *
     * @param email the user's email.
     * @param minutesValid the duration (in minutes) the temp password is valid.
     * @return the generated temporary plain password.
     * @throws SQLException if the user is not found or the update fails.
     */
    public static String generateAndSaveTempPassword(String email, int minutesValid) throws SQLException {
        String tempPlain = generateRandomPassword(8); // e.g. 8-chars
        String hash = BCrypt.hashpw(tempPlain, BCrypt.gensalt(12));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(minutesValid);
        String sql = "UPDATE users SET temp_password_hash = ?, temp_password_expiry = ?, must_change_password = ? WHERE email = ?";
        try (Connection con = DBUtil.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setTimestamp(2, Timestamp.valueOf(expiry));
            ps.setBoolean(3, true); // force change after login
            ps.setString(4, email);
            int rows = ps.executeUpdate();
            if (rows == 1) {
                return tempPlain;
            } else {
                throw new SQLException("User not found or update failed.");
            }
        }
    }

    
    
    /**
     * Verifies whether a provided temporary password is valid for the given email.
     *
     * @param email the user's email.
     * @param providedPassword the temporary password provided by the user.
     * @return true if valid and not expired, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public static boolean verifyTempPassword(String email, String providedPassword) throws SQLException {
        Optional<User> ou = findByEmail(email);
        if (!ou.isPresent()) return false;
        User u = ou.get();
        if (u.getTempPasswordHash() == null || u.getTempPasswordExpiry() == null) return false;
        if (u.getTempPasswordExpiry().before(new Date())) return false;
        return BCrypt.checkpw(providedPassword, u.getTempPasswordHash());
    }

  
    
    /**
     * Clears the temporary password and its expiry for a user.
     *
     * @param email the user's email.
     */
    public static void clearTempPassword(String email) {
        String sql = "UPDATE users SET temp_password_hash = NULL, temp_password_expiry = NULL WHERE email = ?";
        try (Connection con = DBUtil.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    
    /**
     * Updates a user's password to a new one and clears any temporary password.
     *
     * @param email the user's email.
     * @param newPlainPassword the new plain text password.
     * @return true if the password was successfully updated, false otherwise.
     */
    public static boolean updatePassword(String email, String newPlainPassword) {
        String hash = BCrypt.hashpw(newPlainPassword, BCrypt.gensalt(12));
        String sql = "UPDATE users SET password_hash = ?, must_change_password = ?, temp_password_hash = NULL, temp_password_expiry = NULL WHERE email = ?";
        try (Connection con = DBUtil.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setBoolean(2, false);
            ps.setString(3, email);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    /**
     * Generates a random alphanumeric password.
     *
     * @param len the length of the password.
     * @return the generated password as a string.
     */
    private static String generateRandomPassword(int len) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    
    /**
     * Creates a new user entry without a password, forcing them to change it on first login.
     *
     * @param email the email of the new user.
     * @return true if the entry was created successfully, false otherwise.
     */
    public static boolean createNewUserEntry(String email) {
        String sql = "INSERT INTO users (email, must_change_password) VALUES (?, ?)";
        try (Connection con = DBUtil.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setBoolean(2, true); // New users must change password
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
