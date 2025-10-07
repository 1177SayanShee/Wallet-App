<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,com.walletapp.dao.RecordDAO,com.walletapp.model.Record,com.walletapp.model.Label" %>
<%@ page import="com.walletapp.dao.*" %>
<%@ page import="com.walletapp.model.*" %>

<%
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    RecordDAO recordDAO = new RecordDAO();
    List<Record> records = recordDAO.getRecordsByUserId(userId);

    AccountDAO accountDAO = new AccountDAO();
    List<Account> accounts = accountDAO.getAccountsByUserId(userId);

    LabelDAO labelDAO = new LabelDAO();
    List<Label> labels = labelDAO.getLabelsByUserId(userId);
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Records</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

<style>
:root{
  --primary-green:#28a745;
  --bg:#f4f6f8;
  --card-border:#e9ecef;
  --income-green:#a3e4a1;
  --expense-red:#f5a3a3;
}
body {
  font-family: 'Inter', sans-serif;
  background: var(--bg);
  margin: 0;
  color: #212529;
}
.main-wrapper { display: flex; min-height: 100vh; }

/* Sidebar */
.sidebar {
  width: 260px; background: #fff; padding: 20px; border-right: 1px solid var(--card-border);
  position: fixed; top: 0; left: 0; height: 100vh; box-sizing: border-box;
}
.sidebar .logo { width: 44px; height: 44px; background: var(--primary-green); border-radius: 10px; margin-bottom: 18px; }
.sidebar h2 { font-size: 20px; margin: 0 0 12px 0; }
.sidebar .filter-form { margin-top: 20px; }

/* Main */
.main-content { margin-left: 260px; width: calc(100% - 260px); padding: 24px; box-sizing: border-box; }

/* Header/nav */
.header { display:flex; justify-content:space-between; align-items:center; margin-bottom: 16px; }
.main-nav a { color: #6c757d; text-decoration: none; padding: 8px 12px; border-radius: 8px; margin-right:6px; display:inline-block; }
.main-nav a.active, .main-nav a:hover { background: #e9f5ec; color: var(--primary-green); }

/* cards */
.card-dashboard {
  border-radius: 14px;
  padding: 18px;
  border: 1px solid var(--card-border);
  box-shadow: 0 10px 30px rgba(16,24,40,0.06);
  margin-bottom: 20px;
  transition: transform .12s ease, box-shadow .12s ease;
}
.card-dashboard:hover {
  transform: translateY(-4px);
  box-shadow: 0 14px 36px rgba(16,24,40,0.08);
}

/* record card - full width with flexible spacing */
.record-card .card-body {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 12px 24px; /* vertical and horizontal gap */
  font-size: 0.95rem;
}
.record-card .card-body > div {
  flex: 1 1 150px; /* grow, shrink, min-width */
  min-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* modal tweaks */
.modal-content { border-radius: 12px; }
</style>
</head>
<body>
<div class="main-wrapper">
  <!-- Sidebar -->
  <aside class="sidebar">
    <div class="logo"></div>
    <h2>Dashboard</h2>

    <!-- Filter form -->
    <div class="filter-form">
      <form method="get" action="RecordsServlet">
        <div class="mb-3">
          <label class="form-label">Account</label>
          <select name="accountId" class="form-select">
            <option value="">All Accounts</option>
            <%
              Integer selectedAccountId = (Integer) request.getAttribute("selectedAccountId");
              if (accounts != null) {
                  for (Account acc : accounts) {
                      String selected = (selectedAccountId != null && selectedAccountId == acc.getId()) ? "selected" : "";
            %>
              <option value="<%= acc.getId() %>" <%= selected %>><%= acc.getName() %></option>
            <%
                  }
              }
            %>
          </select>
        </div>
        <div class="mb-3">
          <label class="form-label">From</label>
          <input type="date" class="form-control" name="fromDate" value="<%= request.getAttribute("fromDate") != null ? request.getAttribute("fromDate") : "" %>">
        </div>
        <div class="mb-3">
          <label class="form-label">To</label>
          <input type="date" class="form-control" name="toDate" value="<%= request.getAttribute("toDate") != null ? request.getAttribute("toDate") : "" %>">
        </div>
        <div>
          <button class="btn btn-success w-100" type="submit">Filter</button>
        </div>
      </form>
    </div>
  </aside>

  <!-- Main content -->
  <main class="main-content">
    <header class="header">
      <nav class="main-nav">
        <a href="DashboardServlet">Dashboard</a>
        <a href="accounts.jsp">Accounts</a>
        <a href="#" class="active">Records</a>
      </nav>
      
      <!--  -->
				<div class="header-actions d-flex align-items-center">
                    <button class="btn btn-success me-3" data-bs-toggle="modal" data-bs-target="#addRecordModal">+ Record</button>
                    
                    <!-- User Profile Dropdown -->
                    <div class="dropdown">
                        <a class="d-flex align-items-center text-decoration-none dropdown-toggle" href="#" role="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false" style="font-weight: 600; color: #6c757d;">
                            <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28"
                                fill="#6c757d" viewBox="0 0 16 16">
                                <path d="M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0z" />
                                <path fill-rule="evenodd"
                                    d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8zm8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1z" />
                            </svg>
                            <span class="ms-2">Sayan Shee</span>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                            <li><a class="dropdown-item" href="LogoutServlet">Logout</a></li>
                        </ul>
                    </div>
                </div>
				
				<!--  -->
    </header>

    <!-- Record Cards -->
    <div class="row mt-4">
      <%
        if (records != null && !records.isEmpty()) {
            for (Record rec : records) {
                String cardColor = rec.getRecordType().equalsIgnoreCase("Income") ? "var(--income-green)" : "var(--expense-red)";
                String amountSign = rec.getRecordType().equalsIgnoreCase("Income") ? "+" : "-";
      %>
        <div class="col-12 mb-3">
          <div class="card card-dashboard record-card" style="background:<%=cardColor%>;">
            <div class="card-body">
              <div>Category: <strong><%= rec.getCategory() %></strong></div>
              <div>Type: <strong><%= rec.getRecordType() %></strong></div>
              <div>Amount: <strong><%= amountSign %>₹<%= rec.getAmount() %></strong></div>
              <div>Note: <%= rec.getNote() != null ? rec.getNote() : "-" %></div>
              <div>Account: <%= rec.getAccountName() != null ? rec.getAccountName() : "-" %></div>
              <div>Payment: <%= rec.getPaymentType() != null ? rec.getPaymentType() : "-" %></div>
            </div>
          </div>
        </div>
      <%
            }
        } else {
      %>
        <div class="col-12">
          <div class="card card-dashboard text-center">
            <p class="m-3">No records found. Add one to get started.</p>
          </div>
        </div>
      <%
        }
      %>
    </div>

    <!-- Add Record Modal -->
    <div class="modal fade" id="addRecordModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-lg">
        <form method="post" action="<%=request.getContextPath()%>/AddRecordServlet" class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Add Record</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body row g-3">

            <!-- Record Type -->
            <div class="col-md-6">
              <label class="form-label">Record Type</label>
              <select name="recordType" class="form-select" required>
                <option value="Income">Income</option>
                <option value="Expense">Expense</option>
              </select>
            </div>

            <!-- Amount -->
            <div class="col-md-6">
              <label class="form-label">Amount</label>
              <input type="number" step="0.01" name="amount" class="form-control" required>
            </div>

            <!-- Account -->
            <div class="col-md-6">
              <label class="form-label">Account</label>
              <select name="accountId" class="form-select" required>
                <%
                for (Account acc : accounts) {
                %>
                  <option value="<%=acc.getId()%>"><%=acc.getName()%></option>
                <%
                }
                %>
              </select>
            </div>

            <!-- Category -->
            <div class="col-md-6">
              <label class="form-label">Category</label>
              <select name="category" class="form-select" required>
                <option>Food</option>
                <option>Transport</option>
                <option>Shopping</option>
                <option>Bills</option>
                <option>Entertainment</option>
                <option>Health</option>
                <option>Salary</option>
                <option>Others</option>
              </select>
            </div>

            <!-- Note -->
            <div class="col-md-12">
              <label class="form-label">Note</label>
              <textarea name="note" class="form-control"></textarea>
            </div>

            <!-- Payer -->
            <div class="col-md-6">
              <label class="form-label">Payer</label>
              <input type="text" name="payer" class="form-control">
            </div>

            <!-- Payment Type -->
            <div class="col-md-6">
              <label class="form-label">Payment Type</label>
              <select name="paymentType" class="form-select" required>
                <option value="Cash">Cash</option>
                <option value="Card">Card</option>
                <option value="UPI">UPI</option>
                <option value="Bank Transfer">Bank Transfer</option>
                <option value="Other">Other</option>
              </select>
            </div>

            <!-- Payment Status -->
            <div class="col-md-6">
              <label class="form-label">Payment Status</label>
              <select name="paymentStatus" class="form-select" required>
                <option value="Cleared" selected>Cleared</option>
                <option value="Pending">Pending</option>
                <option value="Failed">Failed</option>
              </select>
            </div>

            <!-- Date Time -->
            <div class="col-md-6">
              <label class="form-label">Date & Time (dd/MM/yyyy HH:mm)</label>
              <input type="text" name="dateTime" class="form-control" placeholder="05/10/2025 14:30" required>
            </div>

            <!-- Labels Dropdown -->
            <div class="col-md-12">
              <label class="form-label">Labels</label>
              <div class="d-flex">
                <select name="labels" id="labelsDropdown" class="form-select me-2" multiple size="4">
                  <%
                  for (Label lbl : labels) {
                  %>
                  <option value="<%=lbl.getLabelId()%>"><%=lbl.getLabelName()%></option>
                  <%
                  }
                  %>
                </select>
                <button type="button" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#addLabelModal">+ Add Label</button>
              </div>
              <small class="text-muted">Hold CTRL (Windows) or CMD (Mac) to select multiple labels.</small>
            </div>

          </div>
          <div class="modal-footer">
            <button type="submit" class="btn btn-primary">Save Record</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Add Label Modal -->
    <div class="modal fade" id="addLabelModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog">
        <form id="addLabelForm" class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Add Label</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">Label Name</label>
              <input type="text" id="labelName" name="labelName" class="form-control" required>
            </div>
            <div class="mb-3">
              <label class="form-label">Color</label>
              <input type="color" id="color" name="color" class="form-control form-control-color" value="#ff0000">
            </div>
          </div>
          <div class="modal-footer">
            <button type="submit" class="btn btn-success">Save Label</button>
          </div>
        </form>
      </div>
    </div>

  </main>
</div>


  </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
document.getElementById("addLabelForm")?.addEventListener("submit", function(e) {
    e.preventDefault();
    const formData = new URLSearchParams(new FormData(this));
    fetch("AddLabelServlet", {
        method: "POST",
        body: formData
    })
    .then(res => res.json())
    .then(data => {
        const option = document.createElement("option");
        option.value = data.labelId;
        option.text = data.labelName;
        option.selected = true;
        document.getElementById("labelsDropdown").appendChild(option);

        const modalEl = document.getElementById("addLabelModal");
        const modalInstance = bootstrap.Modal.getOrCreateInstance(modalEl);
        modalInstance.hide();

        const backdrops = document.querySelectorAll('.modal-backdrop');
        backdrops.forEach(b => b.remove());

        this.reset();
    })
    .catch(err => console.error("Error:", err));
});
</script>
</body>
</html>
