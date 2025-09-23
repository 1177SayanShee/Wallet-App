package com.walletapp.servlets;

import com.walletapp.dao.UserDAO;
import com.walletapp.util.EmailUtil;
import jakarta.mail.MessagingException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/GenerateTempPasswordServlet")
public class GenerateTempPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String email = request.getParameter("email");

        try {
            var userOptional = UserDAO.findByEmail(email);

            if (userOptional.isPresent()) {
                // Existing user
                var user = userOptional.get();

                if (user.isMustChangePassword()) {
                    // User needs to verify temp password → generate a new one
                    String tempPassword = UserDAO.generateAndSaveTempPassword(email, 15);

                    String subject = "Your Wallet App Temporary Password";
                    String body = "Hello,\n\nYour temporary password for BudgetBakers is: " + tempPassword +
                                  "\n\nThis password is valid for 15 minutes. Please use it to verify your email and set a new password.\n\n" +
                                  "Thank you,\nBudgetBakers Team";

                    try {
                        EmailUtil.sendEmail(email, subject, body);
                    } catch (MessagingException e) {
                        System.out.println("Error sending temporary password email to " + email);
                        e.printStackTrace();
                    }

                    request.setAttribute("showTempForm", "true");
                    request.setAttribute("email", email);
                    request.setAttribute("message", "A new temporary password has been sent to your email. Please verify it below.");
                    request.getRequestDispatcher("index.jsp").forward(request, response);

                } else {
                    // Regular existing user, proceed to login
                    request.setAttribute("email", email);
                    request.setAttribute("message", "Welcome back! Please enter your password.");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                }

            } else {
                // New user: create entry and generate temp password
                if (UserDAO.createNewUserEntry(email)) {
                    String tempPassword = UserDAO.generateAndSaveTempPassword(email, 15);

                    String subject = "Your Wallet App Temporary Password";
                    String body = "Hello,\n\nYour temporary password for BudgetBakers is: " + tempPassword +
                                  "\n\nThis password is valid for 15 minutes. You will be prompted to change it upon your first login.\n\n" +
                                  "Thank you,\nBudgetBakers Team";

                    try {
                        EmailUtil.sendEmail(email, subject, body);
                    } catch (MessagingException e) {
                        System.out.println("Error sending temporary password email to " + email);
                        e.printStackTrace();
                    }

                    request.setAttribute("showTempForm", "true");
                    request.setAttribute("email", email);
                    request.setAttribute("message", "A temporary password has been generated for your new account. Please enter it below.");
                    request.getRequestDispatcher("index.jsp").forward(request, response);

                } else {
                    request.setAttribute("message", "Failed to create a new user account. Please try again.");
                    request.getRequestDispatcher("index.jsp").forward(request, response);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("message", "A database error occurred. Please try again.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
}
