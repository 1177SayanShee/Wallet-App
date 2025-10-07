package com.walletapp.dao;

import com.walletapp.model.Account;
import com.walletapp.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * The {@code AccountDAO} class provides methods for performing CRUD (Create, Read, Update, Delete)
 * operations on the {@code accounts} table in the database.
 * <p>
 * It handles the persistence and retrieval of {@link Account} objects for a specific user.
 * Database connections are obtained through the {@link DBUtil} utility class.
 * </p>
 *
 * <p><strong>Responsibilities:</strong></p>
 * <ul>
 *   <li>Insert new account records into the database.</li>
 *   <li>Retrieve all accounts belonging to a specific user.</li>
 * </ul>
 *
 * <p><strong>Thread Safety:</strong> This class is not thread-safe. Each method obtains
 * its own database connection and should be used within the servlet request scope.</p>
 *
 * @author Sayan
 * @version 1.0
 * @since 2025-10-07
 */
public class AccountDAO {

	/**
     * Inserts a new account record into the {@code accounts} table.
     * <p>
     * This method stores details such as account name, type, initial balance, currency,
     * color, and whether the account should be excluded from statistics.
     * </p>
     *
     * @param account the {@link Account} object containing the account details to be inserted
     * @throws SQLException if a database access error occurs or the SQL statement fails
     *
     * @see com.walletapp.model.Account
     * @see com.walletapp.util.DBUtil
     */
    public void insertAccount(Account account) throws SQLException {
        String sql = "INSERT INTO accounts " +
                     "(user_id, account_name, account_type, initial_amount, currency, color, exclude_from_statistics, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, account.getUserId());
            ps.setString(2, account.getName());
            ps.setString(3, account.getAccountType());
            ps.setBigDecimal(4, account.getInitialBalance());
            ps.setString(5, account.getCurrency());
            ps.setString(6, account.getColor());
            ps.setBoolean(7, account.getExcludeFromStats() != null && account.getExcludeFromStats());
            ps.setTimestamp(8, Timestamp.valueOf(account.getCreatedAt()));

            ps.executeUpdate();
        }
    }

    /**
     * Retrieves all account records belonging to a specific user.
     * <p>
     * Each record is mapped into an {@link Account} object and returned as a list.
     * </p>
     *
     * @param userId the ID of the user whose accounts should be fetched
     * @return a {@link List} of {@link Account} objects associated with the given user ID
     * @throws SQLException if a database access error occurs or the SQL query fails
     */
    public List<Account> getAccountsByUserId(int userId) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_id, user_id, account_name, account_type, initial_amount, currency, color, exclude_from_statistics, created_at " +
                     "FROM accounts WHERE user_id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Account acc = new Account();
                    acc.setId(rs.getInt("account_id"));
                    acc.setUserId(rs.getInt("user_id"));
                    acc.setName(rs.getString("account_name"));
                    acc.setAccountType(rs.getString("account_type"));
                    acc.setInitialBalance(rs.getBigDecimal("initial_amount"));
                    acc.setCurrency(rs.getString("currency"));
                    acc.setColor(rs.getString("color"));
                    acc.setExcludeFromStats(rs.getBoolean("exclude_from_statistics"));
                    acc.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                    accounts.add(acc);
                }
            }
        }
        return accounts;
    }
}
