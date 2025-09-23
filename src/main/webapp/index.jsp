<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>BudgetBakers - Your Finances in One Place</title>
    <script src="https://accounts.google.com/gsi/client" async defer></script>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif; margin: 0; padding: 0; display: flex; height: 100vh; }
        .left-panel { flex: 1; background-color: #2e7d32; color: white; display: flex; flex-direction: column; justify-content: center; align-items: center; text-align: center; padding: 20px; }
        .left-panel h1 { font-size: 3.5em; margin-bottom: 20px; }
        .left-panel p { font-size: 1.2em; margin-top: 0; max-width: 600px; }
        .left-panel img { max-width: 80%; margin-top: 30px; }
        .right-panel { flex: 1; background-color: #f7f7f7; display: flex; flex-direction: column; justify-content: center; align-items: center; padding: 20px; }
        .login-box { background: white; padding: 40px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); width: 100%; max-width: 400px; text-align: center; }
        .social-login button { width: 100%; padding: 12px; margin-bottom: 15px; border: 1px solid #ddd; border-radius: 5px; background-color: white; font-size: 1em; cursor: pointer; display: flex; align-items: center; justify-content: center; }
        .social-login button img { margin-right: 10px; }
        .separator { margin: 20px 0; font-size: 0.9em; color: #aaa; }
        .separator::before, .separator::after { content: ''; flex: 1; border-bottom: 1px solid #eee; margin: 0 10px; }
        .separator { display: flex; align-items: center; }
        .input-group { margin-bottom: 15px; }
        .input-group input { width: calc(100% - 24px); padding: 12px; border: 1px solid #ccc; border-radius: 5px; font-size: 1em; }
        .btn-login { width: 100%; padding: 12px; border: none; border-radius: 5px; background-color: #2e7d32; color: white; font-size: 1em; cursor: pointer; }
        .message { color: red; margin-top: 10px; }
        .footer-text { position: absolute; bottom: 20px; font-size: 0.8em; color: #888; }
        .footer-text a { color: #2e7d32; text-decoration: none; }
        
                .social-login button {
            width: 100%;
            padding: 12px;
            margin-bottom: 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            background-color: white;
            font-size: 1em;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .social-login button img {
            margin-right: 10px;
        }
        
    </style>
    
    <!-- <link rel="stylesheet" href="styles.css"> -->
</head>

 <body>
    <div class="left-panel">
        <h1>Your Finances in One Place</h1>
        <p>Dive into reports, build budgets, sync with your banks and enjoy automatic categorization.</p>
        <img src="images/wallet_mockup.png" alt="Wallet App Mockup">
    </div>
    <div class="right-panel">
        <div class="login-box">
            <h2>Log In</h2>
            <div class="social-login">
                <div id="g_id_onload"
                     data-client_id="779291629789-nd9ip2k93imo2dot14kjcbbmlpt5fl5d.apps.googleusercontent.com"
                     data-context="signin"
                     data-ux_mode="popup"
                     data-login_uri="http://localhost:8080/WalletApp/GoogleLoginServlet"
                     data-auto_prompt="false">
                </div>
                <div class="g_id_signin"
                     data-type="standard"
                     data-shape="rectangular"
                     data-theme="outline"
                     data-text="continue_with"
                     data-size="large"
                     data-logo_alignment="left">
                </div>

                <button><img src="images/facebook_icon.png" width="20" height="20"> Sign in with Facebook</button>
                <button><img src="images/apple_icon.png" width="20" height="20"> Sign in with Apple</button>
            </div>
            <div class="separator">or</div>
            
            <%-- Conditional form based on whether a temp password is required --%>
            <% String showTempForm = (String) request.getAttribute("showTempForm");
               String email = (String) request.getAttribute("email");
               String message = (String) request.getAttribute("message"); %>

            <% if ("true".equals(showTempForm)) { %>
                <form action="VerifyTempPasswordServlet" method="post">
                    <p>A temporary password has been generated and printed to the console. Please enter it below.</p>
                    <div class="input-group">
                        <input type="text" name="email" value="<%= email %>" readonly style="color: #666; background-color: #f0f0f0;">
                    </div>
                    <div class="input-group">
                        <input type="password" name="tempPassword" placeholder="Enter Temporary Password" required>
                    </div>
                    <button type="submit" class="btn-login">Verify Password</button>
                </form>
            <% } else { %>
                <form action="GenerateTempPasswordServlet" method="post">
                    <div class="input-group">
                        <input type="email" name="email" placeholder="john.doe@budgetbakers.com" required>
                    </div>
                    <button type="submit" class="btn-login">Log In</button>
                </form>
            <% } %>

            <% if (message != null && !message.isEmpty()) { %>
                <p class="message"><%= message %></p>
            <% } %>
        </div>
        <p class="footer-text">By signing up or connecting with the services above you agree to our <a href="#">Terms of Service</a> and acknowledge our <a href="#">Privacy Policy</a> describing how we handle your personal data.</p>
    </div>
</body>
</html>