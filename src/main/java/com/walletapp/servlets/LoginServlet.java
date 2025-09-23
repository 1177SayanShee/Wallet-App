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

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Optional<User> userOptional = UserDAO.findByEmail(email);

        if (userOptional.isPresent()) {
            System.out.println("Login Servlet Hit");
            User user = userOptional.get();

            // ✅ Verify password
            if (BCrypt.checkpw(password, user.getPasswordHash())) {

                // ✅ Store user in session
                HttpSession session = request.getSession();
                session.setAttribute("currentUser", user);

                // ✅ Check must_change_password flag
                if (user.isMustChangePassword()) {
                    // Redirect to RegisterServlet (or Change Password page)
                    // You can also set a message or a flag here
                	System.out.println("RegisterServlet Servlet Hit");
                    response.sendRedirect("RegisterServlet"); 
                } else {
                    // Normal flow to dashboard
                    response.sendRedirect("dashboard.jsp");
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
