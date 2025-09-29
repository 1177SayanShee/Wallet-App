<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>User Login</title>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #f7f7f7; }
        .login-box { background: white; padding: 40px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); width: 100%; max-width: 400px; text-align: center; }
        .login-box h2 { margin-bottom: 20px; }
        .input-group { margin-bottom: 15px; }
        .input-group input { width: calc(100% - 24px); padding: 12px; border: 1px solid #ccc; border-radius: 5px; font-size: 1em; }
        .btn-login { width: 100%; padding: 12px; border: none; border-radius: 5px; background-color: #2e7d32; color: white; font-size: 1em; cursor: pointer; }
        .message { color: red; margin-top: 10px; }
    </style>
</head>
<body>
    <div class="login-box">
        <h2>Log In</h2>
        <% String email = (String) request.getAttribute("email");
           String message = (String) request.getAttribute("message"); %>
        
        <form action="LoginServlet" method="post">
            <div class="input-group">
               <%--  <input type="email" name="email" value="<%= email != null ? email : "" %>" readonly> --%>
                <input type="email" name="email" value="<%= email != null ? email : "" %>" placeholder="Enter your email">
            </div>
            <div class="input-group">
                <input type="password" name="password" placeholder="Enter your password" required>
            </div>
            <button type="submit" class="btn-login">Log In</button>
        </form>
        
        <% if (message != null && !message.isEmpty()) { %>
            <p class="message"><%= message %></p>
        <% } %>
    </div>
</body>
</html>