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

@WebServlet("/VerifyTempPasswordServlet")
public class VerifyTempPasswordServlet extends HttpServlet {

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