<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.walletapp.model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif; display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100vh; background-color: #e8f5e9; color: #333; }
        .dashboard-container { text-align: center; padding: 40px; background-color: white; border-radius: 10px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); }
        h1 { color: #2e7d32; font-size: 3em; }
        p { font-size: 1.2em; }
        .btn-logout { margin-top: 20px; padding: 12px 24px; border: none; border-radius: 5px; background-color: #2e7d32; color: white; text-decoration: none; font-size: 1em; }
    </style>
</head>
<body>
    <% 
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
    %>
    <div class="dashboard-container">
        <h1>Welcome to Your Dashboard, <%= user.getEmail() %>!</h1>
        <p>Your password has been successfully set. You can now manage your finances here.</p>
        <p>This is a placeholder for your actual dashboard content.</p>
        <a href="LogoutServlet" class="btn-logout">Logout</a>
    </div>
</body>
</html>