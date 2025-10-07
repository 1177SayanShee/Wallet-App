package com.walletapp.servlets;

import com.walletapp.dao.UserDAO;
import com.walletapp.model.User;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;


/**
 * Servlet responsible for handling user authentication and login flow.
 *
 * <p>This servlet processes user login requests by verifying credentials against stored
 * user data in the database. It supports both regular users and those required to
 * change their password after receiving a temporary one.</p>
 *
 * <p><b>Workflow:</b></p>
 * <ul>
 *   <li>Retrieves email and password from the login form.</li>
 *   <li>Fetches the corresponding user record via {@link com.walletapp.dao.UserDAO}.</li>
 *   <li>Validates the password using BCrypt hashing.</li>
 *   <li>Creates a user session and redirects appropriately based on user flags.</li>
 * </ul>
 *
 * <p>If authentication fails (invalid password or unknown email), the user is returned
 * to the login page with an error message.</p>
 *
 * @author Sayan
 * @version 1.0
 * @since 2025-10-07
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

	 /**
     * Handles POST requests for user login.
     * 
     * <p>This method authenticates the user based on the provided email and password.
     * If successful, it initiates an HTTP session and redirects to either:
     * <ul>
     *   <li>{@code RegisterServlet} — if the user must change their password, or</li>
     *   <li>{@code DashboardServlet} — for regular authenticated users.</li>
     * </ul>
     * </p>
     * 
     * @param request  the {@link HttpServletRequest} containing user credentials
     * @param response the {@link HttpServletResponse} used for redirection or forwarding
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if input or output errors occur
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Optional<User> userOptional = UserDAO.findByEmail(email);

        if (userOptional.isPresent()) {
            
            User user = userOptional.get();

            // ✅ Verify password
            if (BCrypt.checkpw(password, user.getPasswordHash())) {

                // ✅ Store user in session
                HttpSession session = request.getSession();
                session.setAttribute("currentUser", user);
                session.setAttribute("userId", user.getId());

                // ✅ Check must_change_password flag
                if (user.isMustChangePassword()) {
                    // Redirect to RegisterServlet (or Change Password page)
                    // You can also set a message or a flag here
                	System.out.println("RegisterServlet Servlet Hit");
                    response.sendRedirect("RegisterServlet"); 
                } else {
                    // Normal flow to dashboard
                	response.sendRedirect("DashboardServlet");
                }

            } else {
                // ❌ Invalid password
                request.setAttribute("email", email);
                request.setAttribute("message", "Invalid password. Please try again.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } else {
            // ❌ User not found
            request.setAttribute("message", "No account found with this email.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
