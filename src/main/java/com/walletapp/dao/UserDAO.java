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

public class UserDAO {

    public static Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection con = DBUtil.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
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

    // Generate and store temp password hash and expiry (e.g. 15 minutes)
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

    public static boolean verifyTempPassword(String email, String providedPassword) throws SQLException {
        Optional<User> ou = findByEmail(email);
        if (!ou.isPresent()) return false;
        User u = ou.get();
        if (u.getTempPasswordHash() == null || u.getTempPasswordExpiry() == null) return false;
        if (u.getTempPasswordExpiry().before(new Date())) return false;
        return BCrypt.checkpw(providedPassword, u.getTempPasswordHash());
    }

    // After successful temp login, clear temp password? We'll keep it but expiry may be in past.
    public static void clearTempPassword(String email) {
        String sql = "UPDATE users SET temp_password_hash = NULL, temp_password_expiry = NULL WHERE email = ?";
        try (Connection con = DBUtil.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

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

    private static String generateRandomPassword(int len) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

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
