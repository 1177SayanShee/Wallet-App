package com.walletapp.dao;

import com.walletapp.model.Label;
import com.walletapp.model.Record;
import com.walletapp.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Data Access Object (DAO) for handling operations related to {@link Record} entities.
 * Provides methods to fetch records for a user and to add new records with associated labels.
 */
public class RecordDAO {
	 /**
     * Fetches all records for a specific user, including account details and associated labels.
     * <p>
     * Each Record object in the returned list is populated with its corresponding labels.
     * The results are ordered by record date in descending order.
     * </p>
     *
     * @param userId the ID of the user whose records are to be fetched.
     * @return a {@link List} of {@link Record} objects for the given user.
     * @throws SQLException if a database access error occurs.
     */
    public List<Record> getRecordsByUserId(int userId) throws SQLException {
        Map<Integer, Record> recordMap = new LinkedHashMap<>(); // Preserves insertion order
        String sql = "SELECT r.*, a.account_name, a.color as account_color, l.label_id, l.label_name, l.color as label_color " +
                     "FROM records r " +
                     "LEFT JOIN accounts a ON r.account_id = a.account_id " +
                     "LEFT JOIN record_labels rl ON r.record_id = rl.record_id " +
                     "LEFT JOIN labels l ON rl.label_id = l.label_id " +
                     "WHERE r.user_id = ? ORDER BY r.record_date DESC";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int recordId = rs.getInt("record_id");
                Record record = recordMap.get(recordId);

                if (record == null) {
                    record = new Record();
                    record.setRecordId(recordId);
                    record.setCategory(rs.getString("category"));
                    record.setAmount(rs.getBigDecimal("amount"));
                    record.setNote(rs.getString("note"));
                    record.setRecordDate(rs.getTimestamp("record_date"));
                    record.setAccountName(rs.getString("account_name"));
                    record.setAccountColor(rs.getString("account_color"));
                    record.setRecordType(rs.getString("record_type"));
                    record.setPaymentType(rs.getString("payment_type"));
                    record.setPaymentStatus(rs.getString("payment_status"));
                    recordMap.put(recordId, record);
                }

                if (rs.getInt("label_id") != 0) {
                    Label label = new Label(rs.getInt("label_id"), rs.getString("label_name"), rs.getString("label_color"));
                    record.getLabels().add(label);
                }
            }
        }
        return new ArrayList<>(recordMap.values());
    }

    /**
     * Adds a new record for a user along with its associated labels.
     * <p>
     * This operation is performed within a database transaction to ensure
     * that both the record and its label associations are inserted atomically.
     * </p>
     *
     * @param record the {@link Record} object to be added. Its {@link Label} list
     *               will also be persisted in the record_labels junction table.
     * @throws SQLException if a database access error occurs or the transaction fails.
     */
    public void addRecord(Record record) throws SQLException {
        Connection conn = null;
        try {
            conn = DBUtil.getInstance().getConnection();
            conn.setAutoCommit(false); // Start transaction

            String recordSql = "INSERT INTO records (user_id, record_type, amount, account_id, category, record_date, note, payer, payment_type, payment_status) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            int newRecordId = 0;
            try (PreparedStatement ps = conn.prepareStatement(recordSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, record.getUserId());
                ps.setString(2, record.getRecordType());
                ps.setBigDecimal(3, record.getAmount());
                ps.setInt(4, record.getAccountId());
                ps.setString(5, record.getCategory());
                ps.setTimestamp(6, record.getRecordDate());
                ps.setString(7, record.getNote());
                ps.setString(8, record.getPayer());
                ps.setString(9, record.getPaymentType());
                ps.setString(10, record.getPaymentStatus());
                ps.executeUpdate();
                
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newRecordId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating record failed, no ID obtained.");
                    }
                }
            }

            if (record.getLabels() != null && !record.getLabels().isEmpty()) {
                String junctionSql = "INSERT INTO record_labels (record_id, label_id) VALUES (?, ?)";
                try (PreparedStatement juncPs = conn.prepareStatement(junctionSql)) {
                    for (Label label : record.getLabels()) {
                        juncPs.setInt(1, newRecordId);
                        juncPs.setInt(2, label.getLabelId());
                        juncPs.addBatch();
                    }
                    juncPs.executeBatch();
                }
            }
            
            conn.commit(); // Commit the transaction
        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // Rollback on error
            throw e; 
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}
