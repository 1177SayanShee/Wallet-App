package com.walletapp.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * The {@code LogoutServlet} class handles the user logout process.
 * <p>
 * It invalidates the current user session (if one exists) and redirects the user
 * back to the index page. This ensures all user-related session attributes are
 * cleared, effectively logging the user out of the application.
 * </p>
 *
 * <p><strong>URL Mapping:</strong> {@code /LogoutServlet}</p>
 *
 * @author Sayan
 * @version 1.0
 * @since 2025-10-07
 */
@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {

	/**
     * Handles HTTP GET requests to log out the current user.
     * <p>
     * This method retrieves the current session, invalidates it if it exists,
     * and then redirects the user to the home (index) page.
     * </p>
     *
     * @param request  the {@link HttpServletRequest} object containing client request information
     * @param response the {@link HttpServletResponse} object for sending a response to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an input or output error occurs during redirection
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the current session
        HttpSession session = request.getSession(false);

        // Check if the session exists
        if (session != null) {
            // Invalidate the session to remove all attributes
            session.invalidate();
        }

        // Redirect the user to the index page
        response.sendRedirect("index.jsp");
    }
}