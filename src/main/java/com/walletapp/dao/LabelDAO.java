package com.walletapp.dao;

import com.walletapp.model.Label;
import com.walletapp.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Data Access Object (DAO) class for handling operations on the {@link Label} entity.
 * Provides methods to add a new label and fetch labels by user ID.
 */
public class LabelDAO {

	
	 /**
     * Adds a new label to the database for a specific user.
     *
     * @param label the {@link Label} object containing the user ID, label name, and color.
     * @return the auto-generated ID of the newly inserted label, or -1 if insertion failed.
     * @throws SQLException if a database access error occurs.
     */
	public int addLabel(Label label) throws SQLException {
	    String sql = "INSERT INTO labels (user_id, label_name, color) VALUES (?, ?, ?)";
	    try (Connection conn = DBUtil.getInstance().getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        ps.setInt(1, label.getUserId());
	        ps.setString(2, label.getLabelName());
	        ps.setString(3, label.getColor());
	        ps.executeUpdate();

	        try (ResultSet rs = ps.getGeneratedKeys()) {
	            if (rs.next()) {
	                int generatedId = rs.getInt(1);
	                label.setLabelId(generatedId); // also set it back in the object if needed
	                return generatedId;
	            }
	        }
	    }
	    return -1; // return -1 if insertion failed to get an ID
	}


	
	/**
     * Fetches all labels for a given user.
     *
     * @param userId the ID of the user whose labels are to be fetched.
     * @return a {@link List} of {@link Label} objects associated with the user.
     * @throws SQLException if a database access error occurs.
     */
    public List<Label> getLabelsByUserId(int userId) throws SQLException {
        List<Label> labels = new ArrayList<>();
        String sql = "SELECT * FROM labels WHERE user_id = ?";
        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                labels.add(new Label(
                        rs.getInt("label_id"),
                        rs.getInt("user_id"),
                        rs.getString("label_name"),
                        rs.getString("color")
                ));
            }
        }
        return labels;
    }
}
