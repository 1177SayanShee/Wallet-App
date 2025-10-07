package com.walletapp.servlets;

import com.walletapp.dao.LabelDAO;
import com.walletapp.model.Label;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Enumeration;


/**
 * {@code AddLabelServlet} handles the creation of new labels associated with user transactions.
 * <p>
 * This servlet supports both traditional form submissions and AJAX requests. When triggered,
 * it validates the user session, extracts label data from the request, and persists it via
 * {@link LabelDAO}. Depending on the request type, it either returns a JSON response (for AJAX)
 * or redirects the user to {@code records.jsp}.
 * </p>
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *   <li>Authenticate user session before performing operations.</li>
 *   <li>Parse label name and color parameters from the request.</li>
 *   <li>Persist new label data into the database using {@link LabelDAO}.</li>
 *   <li>Return label details in JSON format for AJAX requests.</li>
 *   <li>Redirect to the records page for normal submissions.</li>
 * </ul>
 *
 * @author Sayan
 * @version 1.0
 * @since 2025-10-07
 */
@WebServlet("/AddLabelServlet")
public class AddLabelServlet extends HttpServlet {
	
	/** DAO responsible for database interactions related to labels. */
    private LabelDAO labelDAO = new LabelDAO();

    
    /**
     * Handles HTTP POST requests to create a new label for the logged-in user.
     * <p>
     * The method validates the user session, reads label details from the request,
     * constructs a {@link Label} object, and saves it in the database. If the request
     * originates from AJAX (determined via the {@code X-Requested-With} header),
     * it returns a JSON response containing the new label details.
     * </p>
     *
     * @param request  the {@link HttpServletRequest} containing label parameters such as name and color
     * @param response the {@link HttpServletResponse} used to return JSON or redirect the client
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an input or output error occurs while sending the response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        System.out.println("Add Label Servlet Hit");
        
     // 🔍 Debugging: dump all received params
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String name = params.nextElement();
            System.out.println("Param: " + name + " = " + request.getParameter(name));
        }

        int userId = (Integer) session.getAttribute("userId");
        String labelName = request.getParameter("labelName");
        String color = request.getParameter("color");
        
        
        System.out.println(labelName);
        System.out.println(color);

        Label label = new Label();
        label.setUserId(userId);
        label.setLabelName(labelName);
        label.setColor(color);
        
        try {
            int newLabelId = labelDAO.addLabel(label);
            System.out.println("New Label ID: " + newLabelId);

            // Check if AJAX
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                String json = String.format(
                    "{\"labelId\": %d, \"labelName\": \"%s\", \"color\": \"%s\"}",
                    newLabelId, label.getLabelName(), label.getColor()
                );

                response.getWriter().write(json);
            } else {
                response.sendRedirect("records.jsp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Important: send JSON error if AJAX
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"Database insert failed\"}");
            } else {
                throw new ServletException("Error adding label", e);
            }
        }
    }
}

