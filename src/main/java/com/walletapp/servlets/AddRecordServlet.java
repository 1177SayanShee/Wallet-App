package com.walletapp.servlets;

import com.walletapp.dao.RecordDAO;
import com.walletapp.model.Label;
import com.walletapp.model.Record;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * Handles the creation and insertion of new financial records for a logged-in user.
 *
 * <p>This servlet processes transaction details submitted from a form or dashboard,
 * constructs a {@link Record} object, and persists it using {@link RecordDAO}.</p>
 *
 * <ul>
 *   <li>Validates the user session.</li>
 *   <li>Parses form parameters such as amount, category, account, payment details, and date.</li>
 *   <li>Converts date strings into {@link java.sql.Timestamp} objects.</li>
 *   <li>Associates selected {@link Label} objects with the record.</li>
 *   <li>Persists record data using {@link RecordDAO} and redirects to the records overview page.</li>
 * </ul>
 *
 * <p><b>Error Handling:</b> Invalid input formats are caught and redirected gracefully with feedback.</p>
 *
 * @author Sayan
 * @version 1.0
 * @since 2025-10-07
 */
@WebServlet("/AddRecordServlet")
public class AddRecordServlet extends HttpServlet {
	
	/**
	 * Default constructor for {@code AddRecordServlet}.
	 * <p>
	 * Initializes the servlet instance. The {@link #init()} method is called
	 * automatically by the servlet container to initialize the DAO.
	 * </p>
	 */
	public AddRecordServlet() {
	    super();
	}
	
	
	/** DAO for managing database interactions related to financial records. */
    private RecordDAO recordDAO;

    
    /**
     * Initializes the {@link RecordDAO} instance for this servlet.
     * <p>
     * This method is invoked once when the servlet is first loaded.
     * </p>
     */
    @Override
    public void init() {
        recordDAO = new RecordDAO();
    }

    /**
     * Handles HTTP POST requests for adding new financial records.
     * <p>
     * This method validates the session, extracts all record details (including labels),
     * converts the input date into a {@link Timestamp}, and delegates database persistence
     * to {@link RecordDAO}. On successful addition, the user is redirected to {@code RecordServlet}.
     * </p>
     *
     * @param request  the {@link HttpServletRequest} containing the form data for the record
     * @param response the {@link HttpServletResponse} used to redirect or display error messages
     * @throws ServletException if a database or servlet-specific error occurs
     * @throws IOException      if an I/O error occurs while sending the response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            Record newRecord = new Record();
            newRecord.setUserId(userId);
            newRecord.setRecordType(request.getParameter("recordType"));
            newRecord.setAmount(new BigDecimal(request.getParameter("amount")));
            newRecord.setAccountId(Integer.parseInt(request.getParameter("accountId")));
            newRecord.setCategory(request.getParameter("category"));
            newRecord.setNote(request.getParameter("note"));
            newRecord.setPayer(request.getParameter("payer"));
            newRecord.setPaymentType(request.getParameter("paymentType"));
            newRecord.setPaymentStatus(request.getParameter("paymentStatus"));

            String dateTimeStr = request.getParameter("dateTime");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr, formatter);
            newRecord.setRecordDate(Timestamp.valueOf(localDateTime));
            
            String[] labelIds = request.getParameterValues("labels");
            if (labelIds != null) {
                List<Label> selectedLabels = new ArrayList<>();
                for (String labelId : labelIds) {
                    Label label = new Label();
                    label.setLabelId(Integer.parseInt(labelId));
                    selectedLabels.add(label);
                }
                newRecord.setLabels(selectedLabels);
            }
            
            recordDAO.addRecord(newRecord);
           
            response.sendRedirect("RecordServlet");

        } catch (SQLException e) {
        	e.printStackTrace();
            throw new ServletException("Database error while adding record.", e);
        } catch (Exception e) {
        	e.printStackTrace();
            // Catch broader exceptions for invalid number/date formats
            request.setAttribute("errorMessage", "Invalid data format submitted.");
            response.sendRedirect("records.jsp"); 
        }
    }
}

