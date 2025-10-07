package com.walletapp.servlets;

import com.walletapp.dao.AccountDAO;
import com.walletapp.dao.DashboardDAO;
import com.walletapp.dao.DashboardDAO.AccountMetrics;
import com.walletapp.model.Account;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * {@code DashboardServlet} is responsible for displaying the main financial dashboard for a user.
 * <p>
 * It retrieves key metrics, account summaries, and category-based spending statistics using {@link DashboardDAO}.
 * The servlet also handles optional filters such as date range and account selection to display personalized insights.
 * </p>
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *   <li>Fetch account list for the logged-in user.</li>
 *   <li>Retrieve dashboard metrics, summaries, and category totals.</li>
 *   <li>Support filtering by date range and account ID.</li>
 *   <li>Forward processed data to {@code dashboard.jsp} for display.</li>
 * </ul>
 *
 * @author Sayan
 * @version 1.0
 * @since 2025-10-07
 */
@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {
	
	/** DAO for retrieving dashboard analytics and account summaries. */
    private DashboardDAO dashboardDAO = new DashboardDAO();
    
    /** DAO for handling account-related database interactions. */
    private AccountDAO accountDAO = new AccountDAO();
    
    
    /**
     * Default constructor.
     */
    public DashboardServlet() {
        super();
    }

    
    /**
     * Handles HTTP GET requests to load the user dashboard.
     * <p>
     * This method verifies the session, extracts optional filter parameters (date range and account),
     * retrieves the corresponding financial data, and forwards it to the {@code dashboard.jsp} view.
     * </p>
     *
     * @param request  the {@link HttpServletRequest} object that contains the request data sent by the client
     * @param response the {@link HttpServletResponse} object for sending the response back to the client
     * @throws ServletException if a servlet-specific error occurs while processing the request
     * @throws IOException      if an input or output error is detected when handling the request
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // Date filters
            String fromDateStr = request.getParameter("fromDate");
            String toDateStr = request.getParameter("toDate");
            Date fromDate = (fromDateStr != null && !fromDateStr.isEmpty()) ? Date.valueOf(fromDateStr) : null;
            Date toDate = (toDateStr != null && !toDateStr.isEmpty()) ? Date.valueOf(toDateStr) : null;

            // Account filter
            String accountIdStr = request.getParameter("accountId");
            Integer accountId = (accountIdStr != null && !accountIdStr.isEmpty()) ? Integer.valueOf(accountIdStr) : null;

            // ✅ Fetch all accounts for dropdown
            List<Account> accounts = accountDAO.getAccountsByUserId(userId);
            request.setAttribute("accounts", accounts);
            request.setAttribute("selectedAccountId", accountId);

            // ✅ Fetch metrics and data (filtered if accountId provided)
            List<AccountMetrics> accountMetrics;
            Map<Integer, Map<String, Map<String, BigDecimal>>> categoryTotals;
            Map<String, BigDecimal> summary;

            if (accountId != null) {
                accountMetrics = dashboardDAO.getAccountMetricsForAccount(userId, accountId, fromDate, toDate);
                categoryTotals = dashboardDAO.getCategoryTotalsForAccount(userId, accountId, fromDate, toDate);
                summary = dashboardDAO.getSummaryForAccount(userId, accountId, fromDate, toDate);
            } else {
                accountMetrics = dashboardDAO.getAccountMetrics(userId, fromDate, toDate);
                categoryTotals = dashboardDAO.getCategoryTotalsForUser(userId, fromDate, toDate);
                summary = dashboardDAO.getSummary(userId, fromDate, toDate);
            }

            // ✅ Set attributes
            request.setAttribute("accountMetrics", accountMetrics);
            request.setAttribute("categoryTotals", categoryTotals);
            request.setAttribute("summary", summary);
            request.setAttribute("fromDate", fromDateStr);
            request.setAttribute("toDate", toDateStr);

            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

