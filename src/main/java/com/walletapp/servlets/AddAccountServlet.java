package com.walletapp.servlets;

import com.walletapp.dao.AccountDAO;
import com.walletapp.model.Account;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * {@code AddAccountServlet} handles the creation of new user accounts within the WalletApp system.
 * <p>
 * This servlet processes form submissions from the "Add Account" page. It validates and extracts
 * form inputs, constructs an {@link Account} object, and persists it using {@link AccountDAO}.
 * Upon successful account creation, it redirects the user to the accounts overview page.
 * </p>
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *   <li>Parse form data submitted from the client.</li>
 *   <li>Build and populate an {@link Account} object.</li>
 *   <li>Persist account details into the database via {@link AccountDAO}.</li>
 *   <li>Redirect users to {@code accounts.jsp} upon success.</li>
 * </ul>
 *
 * @author Sayan
 * @version 1.0
 * @since 2025-10-07
 */
@WebServlet("/AddAccountServlet")
public class AddAccountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    
    /**
     * Handles HTTP POST requests for adding a new account.
     * <p>
     * This method retrieves account information from the request parameters, constructs
     * an {@link Account} object, and delegates persistence to the {@link AccountDAO}.
     * If successful, it redirects the user to the {@code accounts.jsp} page.
     * </p>
     *
     * @param request  the {@link HttpServletRequest} containing the account details from the form submission
     * @param response the {@link HttpServletResponse} for sending a redirect or error message to the client
     * @throws ServletException if a servlet-specific error occurs while processing the request
     * @throws IOException      if an input or output error occurs while writing the response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get form parameters
            int userId = Integer.parseInt(request.getParameter("user_id"));
            String accountName = request.getParameter("account_name");
            String color = request.getParameter("color");
            String accountType = request.getParameter("account_type");
            String currency = request.getParameter("currency");
            BigDecimal initialAmount = new BigDecimal(request.getParameter("initial_amount"));
            boolean excludeFromStatistics = "1".equals(request.getParameter("exclude_from_statistics"));

            // Build Account object
            Account account = new Account();
            account.setUserId(userId);
            account.setName(accountName);
            account.setColor(color);
            account.setAccountType(accountType);
            account.setCurrency(currency);
            account.setInitialBalance(initialAmount);
            account.setExcludeFromStats(excludeFromStatistics);
            account.setCreatedAt(LocalDateTime.now());

            // Insert via DAO
            AccountDAO dao = new AccountDAO();
            dao.insertAccount(account);

            // Redirect back to accounts page
            response.sendRedirect("accounts.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error adding account: " + e.getMessage());
        }
    }
}

