package com.walletapp.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("email") == null) {
            resp.sendRedirect(req.getContextPath() + "index.jsp");
            return;
        }
        req.getRequestDispatcher("dashboard.jsp").forward(req, resp);
    }
}
