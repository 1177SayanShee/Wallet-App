package com.walletapp.servlets;

import com.walletapp.dao.UserDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import com.walletapp.model.User;


/**
 * The {@code RegisterServlet} class manages the password creation or update process
 * for users in the WalletApp application.
 * <p>
 * It handles both the display of the registration form (via {@code doGet})
 * and the processing of password updates (via {@code doPost}).
 * This servlet is invoked when a user is required to set or change their password,
 * usually after the login flow detects a {@code mustChangePassword} flag.
 * </p>
 *
 * <p><strong>URL Mapping:</strong> {@code /RegisterServlet}</p>
 *
 * <p><strong>Workflow:</strong></p>
 * <ul>
 *   <li>Displays the password creation form for the user.</li>
 *   <li>Validates and updates the user's password in the database.</li>
 *   <li>On success, stores the user in the session and redirects to the dashboard.</li>
 *   <li>Handles validation errors (like password mismatch) and database update failures gracefully.</li>
 * </ul>
 *
 * @author Sayan
 * @version 1.0
 * @since 2025-10-07
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {


	/**
     * Handles HTTP GET requests to display the password creation form.
     * <p>
     * This method ensures that the user session exists and that an email is stored
     * under the {@code userEmail} attribute. If not, it redirects to the home page.
     * Otherwise, it forwards the user to {@code register.jsp}.
     * </p>
     *
     * @param request  the {@link HttpServletRequest} object containing client request information
     * @param response the {@link HttpServletResponse} object for sending a response to the client
     * @throws ServletException if an error occurs during request forwarding
     * @throws IOException      if an I/O error occurs during redirect or forwarding
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

  
    /**
     * Handles HTTP POST requests for processing the password creation or update.
     * <p>
     * This method validates password inputs, updates the user's password in the database
     * via {@link UserDAO}, and upon success, logs the user in automatically.
     * It also provides error feedback to the user when validation or update fails.
     * </p>
     *
     * @param request  the {@link HttpServletRequest} object containing form data
     * @param response the {@link HttpServletResponse} object for redirecting or forwarding
     * @throws ServletException if a servlet-related error occurs
     * @throws IOException      if an input or output error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        String email = (String) session.getAttribute("userEmail");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("message", "Passwords do not match.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        if (UserDAO.updatePassword(email, newPassword)) {
            // After successful password update, store the user in session and redirect to dashboard
            User user = UserDAO.findByEmail(email).orElse(null);
            if (user != null) {
                session.setAttribute("currentUser", user); // Store the User object in session
                session.removeAttribute("userEmail"); // Clean up the temporary session attribute
                response.sendRedirect("dashboard.jsp"); // Redirect to the dashboard
            } else {
                request.setAttribute("message", "Registration successful, but user data not found. Please log in.");
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("message", "Failed to update password. Please try again.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}