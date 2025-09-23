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

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    // Handles the GET request to show the password creation form
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    // Handles the POST request to process the new password
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