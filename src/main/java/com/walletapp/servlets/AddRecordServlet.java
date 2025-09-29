package com.walletapp.servlets;

import com.walletapp.util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/AddRecord")  // matches your form action="AddRecord"
public class AddRecordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ Retrieve form data
        int userId = Integer.parseInt(request.getParameter("user_id"));
        String recordDate = request.getParameter("record_date");
        String account = request.getParameter("account");
        String category = request.getParameter("category");
        String label = request.getParameter("label");
        String paymentType = request.getParameter("payment_type");
        String recordType = request.getParameter("record_type");
        String amountStr = request.getParameter("amount");
        String description = request.getParameter("description");

        // handle null/empty
        if (description == null) description = "";

        // parse amount safely
        double amount = 0.0;
        try {
            if (amountStr != null && !amountStr.isEmpty()) {
                amount = Double.parseDouble(amountStr);
            }
        } catch (NumberFormatException e) {
            amount = 0.0;
        }

        // ✅ Insert into DB
        String sql = "INSERT INTO records (user_id, record_date, account, category, label, payment_type, record_type, amount, description) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, recordDate);
            ps.setString(3, account);
            ps.setString(4, category);
            ps.setString(5, label);
            ps.setString(6, paymentType);
            ps.setString(7, recordType);
            ps.setDouble(8, amount);
            ps.setString(9, description);

            ps.executeUpdate();

            // ✅ Redirect back to records.jsp
            response.sendRedirect("records.jsp");

        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally: forward to error page
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error saving record");
        }
    }
}
