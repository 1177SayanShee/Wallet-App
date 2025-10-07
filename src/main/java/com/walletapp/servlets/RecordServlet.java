package com.walletapp.servlets;

import com.walletapp.dao.*;
import com.walletapp.model.*;
import com.walletapp.model.Record;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * The {@code RecordServlet} class is responsible for retrieving and displaying 
 * user-specific financial records within the WalletApp application.
 * <p>
 * It interacts with multiple DAO classes ({@link RecordDAO}, {@link AccountDAO}, {@link LabelDAO})
 * to fetch data from the database such as user records, accounts, and labels. 
 * The data is then forwarded to the {@code records.jsp} page for rendering.
 * </p>
 *
 * <p><strong>URL Mapping:</strong> {@code /RecordServlet}</p>
 *
 * <p><strong>Workflow:</strong></p>
 * <ul>
 *   <li>Validates user session and ensures the user is logged in.</li>
 *   <li>Fetches all records, accounts, and labels for the logged-in user.</li>
 *   <li>Sets the retrieved data as request attributes.</li>
 *   <li>Forwards the request to the JSP page for display.</li>
 * </ul>
 *
 * @author Sayan
 * @version 1.0
 * @since 2025-10-07
 */
@WebServlet("/RecordServlet")
public class RecordServlet extends HttpServlet {
	
	/** DAO for handling record-related operations. */
    private RecordDAO recordDAO = new RecordDAO();
    
    /** DAO for handling account-related operations. */
    private AccountDAO accountDAO = new AccountDAO();
    
    /** DAO for handling label-related operations. */
    private LabelDAO labelDAO = new LabelDAO();
    
    /**
     * Default constructor.
     * <p>
     * Initializes the servlet and DAO instances used for data access.
     * </p>
     */
    public RecordServlet() {
        super();
    }

    
    /**
     * Handles HTTP GET requests for retrieving and displaying user records.
     * <p>
     * This method checks for a valid user session, fetches records, accounts, and labels 
     * from the database for the logged-in user, and forwards the data to {@code records.jsp}.
     * </p>
     *
     * @param request  the {@link HttpServletRequest} containing the client request information
     * @param response the {@link HttpServletResponse} used to send a response to the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an input or output error is detected when handling the request
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        try {
            List<Record> records = recordDAO.getRecordsByUserId(userId);
            List<Account> accounts = accountDAO.getAccountsByUserId(userId);
            List<Label> allLabels = labelDAO.getLabelsByUserId(userId);
            request.setAttribute("records", records);
            request.setAttribute("accounts", accounts);
            request.setAttribute("allLabels", allLabels);
            request.getRequestDispatcher("records.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}
