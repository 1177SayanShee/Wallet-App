package com.walletapp.servlets;

import com.walletapp.dao.UserDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;


/**
 * The {@code VerifyTempPasswordServlet} class handles verification of a user's
 * temporary password during the login or password recovery process.
 * <p>
 * This servlet checks whether the provided temporary password matches the stored
 * value in the database. If verified successfully, it stores the user's email in
 * the session and redirects them to the {@code RegisterServlet} to set a new password.
 * </p>
 *
 * <p><strong>URL Mapping:</strong> {@code /VerifyTempPasswordServlet}</p>
 *
 * <p><strong>Workflow:</strong></p>
 * <ul>
 *   <li>Receives the user's email and temporary password from the login form.</li>
 *   <li>Validates credentials via {@link UserDAO#verifyTempPassword(String, String)}.</li>
 *   <li>On success, stores {@code userEmail} in session and clears the temporary password.</li>
 *   <li>Redirects the user to the {@code RegisterServlet} to create a new password.</li>
 *   <li>Handles invalid or expired temporary passwords with appropriate error messages.</li>
 * </ul>
 *
 * @author Sayan
 * @version 1.0
 * @since 2025-10-07
 */
@WebServlet("/VerifyTempPasswordServlet")
public class VerifyTempPasswordServlet extends HttpServlet {

	
	 /**
     * Handles HTTP POST requests for verifying a temporary password.
     * <p>
     * This method validates the provided temporary password against the database.
     * If successful, it stores the user's email in session for the registration process
     * and removes the temporary password from the database to prevent reuse.
     * </p>
     *
     * @param request  the {@link HttpServletRequest} containing form data such as email and tempPassword
     * @param response the {@link HttpServletResponse} used for redirects or forwarding
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an input or output error occurs during redirect or forwarding
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String tempPassword = request.getParameter("tempPassword");
        
        try {
            if (UserDAO.verifyTempPassword(email, tempPassword)) {
                // Success: Store email in session to be used by the registration servlet
                HttpSession session = request.getSession();
                session.setAttribute("userEmail", email);
                
                // Clear the temp password hash from the database
                UserDAO.clearTempPassword(email);

                response.sendRedirect("RegisterServlet");
            } else {
                request.setAttribute("showTempForm", "true");
                request.setAttribute("email", email);
                request.setAttribute("message", "Invalid temporary password or it has expired.");
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("message", "Database error occurred.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}