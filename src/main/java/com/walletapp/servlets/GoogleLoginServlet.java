package com.walletapp.servlets;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.walletapp.dao.UserDAO;
import com.walletapp.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;


/**
 * Servlet responsible for handling Google Sign-In authentication and user session management.
 *
 * <p>This servlet verifies the Google ID token received from the client, validates it using
 * Google's OAuth2 library, and either retrieves an existing user or creates a new one in the database.</p>
 *
 * <p><b>Workflow:</b></p>
 * <ul>
 *   <li>Receives the Google ID token sent from the frontend (after successful sign-in).</li>
 *   <li>Validates the token using the Google API client library.</li>
 *   <li>Checks if the user exists in the database via {@link com.walletapp.dao.UserDAO}.</li>
 *   <li>If the user does not exist, creates a new account entry.</li>
 *   <li>Establishes an HTTP session for the authenticated user and redirects to the dashboard.</li>
 * </ul>
 *
 * <p>If verification fails or an error occurs, the user is redirected back to the index page
 * with an appropriate error message.</p>
 *
 * @author Sayan
 * @version 1.0
 * @since 2025-10-07
 */
@WebServlet("/GoogleLoginServlet")
public class GoogleLoginServlet extends HttpServlet {

	/** Google OAuth2 Client ID registered for the Wallet App project. */
    private static final String GOOGLE_CLIENT_ID = "779291629789-nd9ip2k93imo2dot14kjcbbmlpt5fl5d.apps.googleusercontent.com";

    
    /**
     * Handles the POST request sent from the Google Sign-In client script.
     * 
     * <p>This method performs the following:
     * <ul>
     *   <li>Retrieves the Google ID token from the request.</li>
     *   <li>Validates the token's authenticity and extracts user information.</li>
     *   <li>Finds or creates a user record in the database.</li>
     *   <li>Initializes a session and redirects to the dashboard page.</li>
     * </ul>
     * </p>
     *
     * @param request  the {@link HttpServletRequest} containing the ID token parameter
     * @param response the {@link HttpServletResponse} used for redirection or forwarding
     * @throws ServletException if servlet-specific errors occur
     * @throws IOException if input or output errors occur
     */
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String idTokenString = request.getParameter("credential");

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = (String) payload.get("email");
                String name = (String) payload.get("name");

                // Check if user exists in your database
                Optional<User> userOptional = UserDAO.findByEmail(email);
                User user;

                if (userOptional.isPresent()) {
                    user = userOptional.get();
                } else {
                    // New user, create an account
                    // Note: You might want to handle this differently, e.g., create a new
                    // User object and persist it. For this example, we'll use a placeholder.
                    if (UserDAO.createNewUserEntry(email)) {
                        user = UserDAO.findByEmail(email).orElse(null);
                        // A new user has been created with a temporary password and must_change_password set to true.
                        // You might want to redirect them to a password creation page or log them in directly.
                    } else {
                        throw new Exception("Failed to create new user entry.");
                    }
                }

                // Log in the user by creating a session
                HttpSession session = request.getSession();
                session.setAttribute("currentUser", user);
                
                // Redirect to dashboard
                response.sendRedirect("dashboard.jsp");

            } else {
                // Invalid ID token
                request.setAttribute("message", "Invalid Google ID token.");
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "An error occurred during Google sign-in. Please try again.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
}