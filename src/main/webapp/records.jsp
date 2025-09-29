<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
    // ✅ Get userId from session
    Integer userId = (Integer) session.getAttribute("userId");

    // Optional: redirect to login if no userId found
    if (userId == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<%@ page import="java.sql.*,java.util.*" %>
<%@ page import="com.walletapp.util.DBUtil" %>
<%
    // ✅ Get records for this user
    List<Map<String, Object>> records = new ArrayList<>();
    String sql = "SELECT * FROM records WHERE user_id=? ORDER BY record_date DESC";

    try (Connection conn = DBUtil.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> rec = new HashMap<>();
                rec.put("record_date", rs.getDate("record_date"));
                rec.put("account", rs.getString("account"));
                rec.put("category", rs.getString("category"));
                rec.put("label", rs.getString("label"));
                rec.put("payment_type", rs.getString("payment_type"));
                rec.put("record_type", rs.getString("record_type"));
                rec.put("amount", rs.getBigDecimal("amount"));
                rec.put("description", rs.getString("description"));
                records.add(rec);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
%>


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Records</title>
<style>
    body {
        margin: 0;
        font-family: Arial, sans-serif;
        background-color: #f7f7f7;
    }
    .header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 15px 20px;
        background: #fff;
        border-bottom: 1px solid #ddd;
    }
    .header h1 {
        font-size: 20px;
        margin: 0;
    }
    .record-btn {
        background: #28a745;
        color: #fff;
        border: none;
        padding: 10px 15px;
        cursor: pointer;
        border-radius: 5px;
    }
    .container {
        display: flex;
        min-height: calc(100vh - 60px);
    }
    .sidebar {
        width: 250px;
        background: #fff;
        padding: 20px;
        border-right: 1px solid #ddd;
    }
    .sidebar h2 {
        font-size: 18px;
        margin-top: 0;
    }
    .sidebar label {
        display: block;
        margin-top: 15px;
        font-weight: bold;
        font-size: 14px;
    }
    .sidebar select, .sidebar input {
        width: 100%;
        padding: 8px;
        margin-top: 5px;
        border: 1px solid #ccc;
        border-radius: 4px;
    }
    .main-content {
        flex: 1;
        padding: 20px;
        text-align: center;
        color: #888;
    }

 
 /* --- Modal --- */
.modal {
    display: none;
    position: fixed;
    z-index: 9999;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    overflow-y: auto;
    background-color: rgba(0,0,0,0.4);
}

/* make modal-content bigger, cleaner */
.modal-content {
    background-color: #fff;
    margin: 3% auto;
    padding: 30px;
    border-radius: 10px;
    width: 600px; /* increased width */
    max-width: 90%;
    box-shadow: 0 8px 30px rgba(0,0,0,0.2);
    position: relative;
    animation: slideDown 0.3s ease-out;
}

/* Close button */
.close {
    color: #888;
    position: absolute;
    top: 15px;
    right: 20px;
    font-size: 24px;
    cursor: pointer;
}

/* Make the form inputs stretch and have consistent look */
.modal-content form {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 15px 20px;
}

.modal-content h2 {
    margin-top: 0;
    grid-column: span 2;
    text-align: center;
    font-size: 24px;
    color: #28a745;
}

/* labels above inputs */
.modal-content label {
    display: block;
    font-size: 14px;
    font-weight: bold;
    margin-bottom: 5px;
    color: #333;
}

/* unify input/select/textarea */
.modal-content input,
.modal-content select,
.modal-content textarea {
    width: 100%;
    padding: 10px 12px;
    border: 1px solid #ccc;
    border-radius: 6px;
    font-size: 14px;
    box-sizing: border-box;
    transition: border-color 0.2s;
}

.modal-content input:focus,
.modal-content select:focus,
.modal-content textarea:focus {
    border-color: #28a745;
    outline: none;
}

/* Textarea full width */
.modal-content textarea {
    grid-column: span 2;
    resize: vertical;
    min-height: 80px;
}

/* Submit button full width */
.modal-content button {
    grid-column: span 2;
    margin-top: 15px;
    background: #28a745;
    color: #fff;
    padding: 12px;
    font-size: 16px;
    border: none;
    cursor: pointer;
    border-radius: 6px;
    transition: background 0.2s;
}

.modal-content button:hover {
    background: #218838;
}

/* simple entrance animation */
@keyframes slideDown {
  from {transform: translateY(-30px); opacity: 0;}
  to {transform: translateY(0); opacity: 1;}
}
 

 .cards-container {
    display: flex;
    flex-direction: column;  
        gap: 15px;
}

.record-card {
    background: #fff;
    padding: 15px;
    border-radius: 8px;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    text-align: left;
    display: block;
    transition: transform 0.2s ease;
}

.record-card:hover {
    transform: translateY(-3px);
}
.record-card div {
    margin-bottom: 6px;
    font-size: 14px;
} */


/* .main-content {
    flex: 1;
    padding: 20px;
}

/* container */
/* Import modern font */
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap');

body {
    font-family: 'Poppins', Arial, sans-serif;
}

/* main container */
.main-content {
    flex: 1;
    padding: 20px;
}

/* cards container */
.cards-container {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

/* record pill */
.record-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 18px 24px; /* larger height */
    border-radius: 40px; /* pill */
    box-shadow: 0 4px 12px rgba(0,0,0,0.12);
    font-size: 15px;
    font-weight: 500;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
}

/* hover effect */
.record-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 16px rgba(0,0,0,0.18);
}

/* left block */
.record-info {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 3px;
}

/* date styling */
.record-date {
    font-size: 13px;
    opacity: 0.85;
    margin-right: 15px;
    font-weight: 400;
}

/* amount styling */
.record-amount {
    font-weight: 600;
    font-size: 17px;
}

/* income variant */
.record-card.income {
    background-color: #c2f5d4; /* deeper green */
    color: #0f5132;
}
.record-card.income .record-amount {
    color: #198754; /* bootstrap green */
}

/* expense variant */
.record-card.expense {
    background-color: #f8c6c6; /* deeper red */
    color: #842029;
}
.record-card.expense .record-amount {
    color: #dc3545; /* bootstrap red */
}

/* category bold + description subtle */
.record-info span:first-child {
    font-size: 16px;
    font-weight: 600;
    color: inherit;
}
.record-info span:last-child {
    font-size: 14px;
    opacity: 0.9;
}




    
</style>


</head>
<body>

<div class="header">
    <h1>Records</h1>
    <button class="record-btn" onclick="openModal()">+ Record</button>
</div>

<div class="container">
    <div class="sidebar">
        <h2>Filters</h2>

        <label>Date</label>
        <input type="date" name="filterDate">

        <label>Account</label>
        <select>
            <option>All Accounts</option>
            <option>Bank A</option>
            <option>Wallet</option>
        </select>

        <label>Categories</label>
        <select>
            <option>All Categories</option>
            <option>Food</option>
            <option>Rent</option>
        </select>

        <label>Labels</label>
        <select>
            <option>All Labels</option>
            <option>Personal</option>
            <option>Business</option>
        </select>

        <label>Payment Type</label>
        <select>
            <option>All Types</option>
            <option>Cash</option>
            <option>Card</option>
        </select>

        <label>Record Types</label>
        <select>
            <option>All Records</option>
            <option>Expense</option>
            <option>Income</option>
        </select>

        <label>Amount</label>
        <input type="number" step="0.01" name="filterAmount" placeholder="Amount">
    </div>

    <div class="main-content">
        <div class="main-content">
    <h2>Your Records</h2>
    <div class="cards-container">
    <%-- <%
        if (records.isEmpty()) {
    %>
        <p>Sorry, no records were found for this combination of filters.</p>
    <%
        } else {
            for (Map<String, Object> r : records) {
                String recordType = (String) r.get("record_type");
                String color = "Expense".equalsIgnoreCase(recordType) ? "#dc3545" : "#28a745"; // red for expense, green for income
    %>
        <div class="record-card" style="border-left:5px solid <%=color%>;">
            <div><strong>Date:</strong> <%=r.get("record_date")%></div>
            <div><strong>Account:</strong> <%=r.get("account")%></div>
            <div><strong>Category:</strong> <%=r.get("category")%></div>
            <div><strong>Label:</strong> <%=r.get("label")%></div>
            <div><strong>Payment:</strong> <%=r.get("payment_type")%></div>
            <div><strong>Type:</strong> <span style="color:<%=color%>;"><%=recordType%></span></div>
            <div><strong>Amount:</strong> <span style="color:<%=color%>;">₹<%=r.get("amount")%></span></div>
            <div><strong>Description:</strong> <%=r.get("description")%></div>
        </div>
    <%
            }
        }
    %> --%>
    
    
    
      <%-- <%
        if (records.isEmpty()) {
    %>
        <p>Sorry, no records were found for this combination of filters.</p>
    <%
        } else {
            for (Map<String, Object> r : records) {
                String recordType = (String) r.get("record_type");
                String color = "Expense".equalsIgnoreCase(recordType) ? "#dc3545" : "#28a745"; // red for expense, green for income
    %>
        <div class="record-card" style="border-left:5px solid <%=color%>;">
            <div><strong>Date:</strong> <%=r.get("record_date")%></div>
            <div><strong>Account:</strong> <%=r.get("account")%></div>
            <div><strong>Category:</strong> <%=r.get("category")%></div>
            <div><strong>Label:</strong> <%=r.get("label")%></div>
            <div><strong>Payment:</strong> <%=r.get("payment_type")%></div>
            <div><strong>Type:</strong> <span style="color:<%=color%>;"><%=recordType%></span></div>
            <div><strong>Amount:</strong> <span style="color:<%=color%>;">₹<%=r.get("amount")%></span></div>
            <div><strong>Description:</strong> <%=r.get("description")%></div>
        </div>
    <%
            }
        }
    %> --%>
    
    
    
    <%
    for (Map<String, Object> r : records) {
        String recordType = (String) r.get("record_type");
        boolean isExpense = "Expense".equalsIgnoreCase(recordType);
        String color = isExpense ? "#dc3545" : "#28a745"; // red for expense, green for income
        String bgColor = isExpense ? "#ffe6e6" : "#e6ffed"; // soft background tint
%>
<div class="record-card" style="border-left:8px solid <%=color%>; background:<%=bgColor%>;">
    <div><strong>Date:</strong> <%=r.get("record_date")%></div>
    <div><strong>Account:</strong> <%=r.get("account")%></div>
    <div><strong>Category:</strong> <%=r.get("category")%></div>
    <div><strong>Label:</strong> <%=r.get("label")%></div>
    <div><strong>Payment:</strong> <%=r.get("payment_type")%></div>
    <div><strong>Type:</strong> <span style="color:<%=color%>;"><%=recordType%></span></div>
    <div><strong>Amount:</strong> <span style="color:<%=color%>;">₹<%=r.get("amount")%></span></div>
    <div><strong>Description:</strong> <%=r.get("description")%></div>
</div>
<%
    }
%>
    
    </div>
</div>

    </div>
</div>

<!-- Modal -->
<%-- <div id="recordModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <h2>Add New Record</h2>
        <form action="AddRecord" method="post">
        
            <!-- hidden field for user id -->
            <input type="hidden" name="user_id" value="<%= userId %>">
            
            <label>Date</label>
            <input type="date" name="record_date" required>

            <label>Account</label>
            <select name="account" required>
                <option>Bank A</option>
                <option>Wallet</option>
            </select>

            <label>Category</label>
            <select name="category" required>
                <option>Food</option>
                <option>Rent</option>
            </select>

            <label>Label</label>
            <select name="label">
                <option>Personal</option>
                <option>Business</option>
            </select>

            <label>Payment Type</label>
            <select name="payment_type">
                <option>Cash</option>
                <option>Card</option>
            </select>

            <label>Record Type</label>
            <select name="record_type">
                <option>Expense</option>
                <option>Income</option>
            </select>

            <label>Amount</label>
            <input type="number" step="0.01" name="amount" placeholder="Enter amount">

            <label>Description</label>
            <textarea name="description" placeholder="Optional description"></textarea>

            <button type="submit">Save Record</button>
        </form>
    </div>
</div> --%>

<!-- Modal -->
<div id="recordModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeModal()">&times;</span>
        <h2>Add New Record</h2>
        <form action="AddRecord" method="post">
        
            <!-- hidden field for user id -->
            <input type="hidden" name="user_id" value="<%= userId %>">
            
            <div>
                <label>Date</label>
                <input type="date" name="record_date" required>
            </div>

            <div>
                <label>Account</label>
                <select name="account" required>
                    <option>Bank A</option>
                    <option>Wallet</option>
                </select>
            </div>

            <div>
                <label>Category</label>
                <select name="category" required>
                    <option>Food</option>
                    <option>Rent</option>
                </select>
            </div>

            <div>
                <label>Label</label>
                <select name="label">
                    <option>Personal</option>
                    <option>Business</option>
                </select>
            </div>

            <div>
                <label>Payment Type</label>
                <select name="payment_type">
                    <option>Cash</option>
                    <option>Card</option>
                </select>
            </div>

            <div>
                <label>Record Type</label>
                <select name="record_type">
                    <option>Expense</option>
                    <option>Income</option>
                </select>
            </div>

            <div>
                <label>Amount</label>
                <input type="number" step="0.01" name="amount" placeholder="Enter amount">
            </div>

            <div style="grid-column: span 2;">
                <label>Description</label>
                <textarea name="description" placeholder="Optional description"></textarea>
            </div>

            <button type="submit">Save Record</button>
        </form>
    </div>
</div>


<script>
    function openModal() {
        document.getElementById('recordModal').style.display = 'block';
    }
    function closeModal() {
        document.getElementById('recordModal').style.display = 'none';
    }
    window.onclick = function(event) {
        if (event.target == document.getElementById('recordModal')) {
            closeModal();
        }
    }
</script>

</body>
</html>
