<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page
	import="com.walletapp.dao.AccountDAO, com.walletapp.model.Account, java.util.List, java.text.NumberFormat, java.util.Locale"%>
<%
Integer userId = (Integer) session.getAttribute("userId");
if (userId == null) {
	response.sendRedirect("login.jsp");
	return;
}

AccountDAO dao = new AccountDAO();
List<Account> accountList = dao.getAccountsByUserId(userId);

// Formatter for currency
/* NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN")); */
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Accounts</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link
	href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap"
	rel="stylesheet">

<style>
/* -- GLOBAL STYLES -- */
:root {
	--primary-green: #28a745; /* A standard green color */
	--light-gray-bg: #f4f6f8;
	--border-color: #e9ecef;
	--text-primary: #212529;
	--text-secondary: #6c757d;
}

body {
	font-family: 'Inter', sans-serif;
	background-color: var(--light-gray-bg);
	color: var(--text-primary);
	margin: 0;
}

.main-wrapper {
	display: flex;
}

/* -- SIDEBAR STYLES -- */
.sidebar {
	width: 260px;
	background-color: #ffffff;
	height: 100vh;
	position: fixed;
	top: 0;
	left: 0;
	padding: 20px;
	border-right: 1px solid var(--border-color);
	display: flex;
	flex-direction: column;
}

.sidebar .logo {
	width: 40px;
	height: 40px;
	background-color: var(--primary-green);
	border-radius: 8px;
	margin-bottom: 40px;
}

.sidebar h2 {
	font-size: 24px;
	font-weight: 600;
}

.sidebar .btn-add-account {
	background-color: var(--primary-green);
	color: white;
	border: none;
	width: 100%;
	padding: 10px;
	font-weight: 500;
	margin-top: 20px;
}

.sidebar .btn-add-account:hover {
	background-color: #218838;
}

.archive-toggle {
	margin-top: 30px;
}

/* -- MAIN CONTENT STYLES -- */
.main-content {
	margin-left: 260px; /* Same as sidebar width */
	width: calc(100% - 260px);
	padding: 20px 30px;
}

/* -- HEADER / TOP NAV -- */
.header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding-bottom: 20px;
}

.main-nav a {
	color: var(--text-secondary);
	text-decoration: none;
	font-weight: 500;
	padding: 8px 16px;
	margin-right: 10px;
	border-radius: 8px;
}

.main-nav a.active, .main-nav a:hover {
	background-color: #e9f5ec;
	color: var(--primary-green);
}

.header-actions {
	display: flex;
	align-items: center;
	gap: 20px;
}

.header-actions .btn-record {
	background-color: var(--primary-green);
	color: white;
	border-radius: 8px;
}

.header-actions .btn-record:hover {
	background-color: #218838;
}

.user-profile {
	font-weight: 500;
}

.user-profile span {
	margin-left: 8px;
}

/* -- ACCOUNTS LIST -- */
.accounts-list {
	margin-top: 20px;
}

.account-row {
	display: flex;
	align-items: center;
	background-color: #ffffff;
	padding: 16px;
	border-radius: 12px;
	margin-bottom: 12px;
	border: 1px solid var(--border-color);
	transition: box-shadow 0.2s ease-in-out;
}

.account-row:hover {
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

/* THIS IS THE PLACEHOLDER FOR YOUR ICONS */
.account-icon-placeholder {
	width: 40px;
	height: 40px;
	border-radius: 8px;
	margin-right: 16px;
	flex-shrink: 0;
}

.account-details {
	flex-grow: 1;
}

.account-details .name {
	font-weight: 600;
	font-size: 16px;
	margin: 0;
}

.account-details .type {
	font-size: 14px;
	color: var(--text-secondary);
	margin: 0;
}

.account-balance {
	font-size: 16px;
	font-weight: 600;
	margin-right: 20px;
}

.account-actions {
	color: var(--text-secondary);
	cursor: pointer;
	font-size: 24px;
	font-weight: bold;
}
</style>
</head>
<body>

	<div class="main-wrapper">
		<aside class="sidebar">
			<div class="logo"></div>

			<h2>Accounts</h2>

			<button class="btn btn-add-account" data-bs-toggle="modal"
				data-bs-target="#accountModal">+ Add</button>

			<div class="archive-toggle form-check form-switch mt-4">
				<input class="form-check-input" type="checkbox" role="switch"
					id="showArchived"> <label class="form-check-label"
					for="showArchived">Show Archived</label>
			</div>
		</aside>

		<main class="main-content">
			<header class="header">
				<nav class="main-nav">
					<a href="DashboardServlet">Dashboard</a> 
					<a href="#" class="active">Accounts</a> 
					<a href="RecordServlet">Records</a>  
					
				</nav>
				<!-- <div class="header-actions">
				     <button class="btn btn-record">+ Record</button>
					<div class="user-profile">
						<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32"
							fill="#6c757d" class="bi bi-person-circle" viewBox="0 0 16 16">
						<path d="M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0z" />
						<path fill-rule="evenodd"
								d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8zm8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1z" />
					</svg>
						<span>Sayan Shee ▼</span>
					</div>
				</div> -->
				
				<!--  -->
				<div class="header-actions d-flex align-items-center">
                    <!-- <button class="btn btn-success me-3" data-bs-toggle="modal" data-bs-target="#addRecordModal">+ Record</button> -->
                    
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

			<section class="accounts-list">
				<%
				if (accountList != null && !accountList.isEmpty()) {
					for (Account acc : accountList) {
				%>
				<div class="account-row">
					<div class="account-icon-placeholder"
						style="background-color: <%=acc.getColor()%>; opacity: 0.3;"></div>

					<div class="account-details">
						<p class="name"><%=acc.getName()%></p>
						<p class="type"><%=acc.getAccountType()%></p>
					</div>

					<div class="account-balance">
						<%
						// Logic to determine currency format based on the account's currency property
						String currencyCode = acc.getCurrency();
						NumberFormat dynamicFormatter;

						if ("USD".equalsIgnoreCase(currencyCode)) {
							dynamicFormatter = NumberFormat.getCurrencyInstance(Locale.US);
						} else if ("EUR".equalsIgnoreCase(currencyCode)) {
							dynamicFormatter = NumberFormat.getCurrencyInstance(Locale.GERMANY); // Using Locale.GERMANY for Euro (€)
						} else if ("GBP".equalsIgnoreCase(currencyCode)) {
							dynamicFormatter = NumberFormat.getCurrencyInstance(Locale.UK);
						} else { // Default to INR (₹)
							dynamicFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
						}

						// Print the formatted balance
						out.print(dynamicFormatter.format(acc.getInitialBalance()));
						%>
					</div>

					<div class="account-actions">&#8942;</div>
				</div>
				<%
				}
				} else {
				%>
				<div class="text-center p-5 bg-white rounded-3">
					<p>No accounts found. Add one to get started.</p>
				</div>
				<%
				}
				%>
			</section>

		</main>
	</div>

	<div class="modal fade" id="accountModal" tabindex="-1"
		aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<form action="AddAccountServlet" method="post">
					<div class="modal-header">
						<h5 class="modal-title">Add Account</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
					</div>
					<div class="modal-body row g-3">

						<input type="hidden" name="user_id" value="<%=userId%>">

						<div class="col-md-6">
							<label class="form-label">Name *</label> <input type="text"
								name="account_name" class="form-control" required>
						</div>

						<div class="col-md-6">
							<label class="form-label">Color</label> <input type="color"
								id="colorPicker" name="color"
								class="form-control form-control-color" value="#AD1457">
							<small id="colorCode" class="text-muted">#AD1457</small>
						</div>

						<div class="col-md-6">
							<label class="form-label">Account Type</label> <select
								name="account_type" class="form-select" required>
								<option value="General">General</option>
								<option value="Cash">Cash</option>
								<option value="Savings account">Savings account</option>
								<option value="Checking account">Checking account</option>
								<option value="Credit account">Credit account</option>
								<option value="Bonus">Bonus</option>
								<option value="Investment account">Investment account</option>
								<option value="Life insurance account">Life insurance
									account</option>
								<option value="Loan">Loan</option>
								<option value="Mortgage">Mortgage</option>
							</select>
						</div>

						<div class="col-md-6">
							<label class="form-label">Initial Amount</label> <input
								type="number" step="0.01" name="initial_amount"
								class="form-control" value="0">
						</div>

						<div class="col-md-6">
							<label class="form-label">Currency</label> <select
								name="currency" class="form-select" required>
								<option value="INR">INR</option>
								<option value="USD">USD</option>
								<option value="EUR">EUR</option>
								<option value="GBP">GBP</option>
							</select>
						</div>

						<div class="col-md-6 align-self-center">
							<div class="form-check form-switch">
								<input class="form-check-input" type="checkbox"
									name="exclude_from_statistics" value="1" id="excludeSwitch">
								<label class="form-check-label" for="excludeSwitch">Exclude
									from statistics</label>
							</div>
						</div>
					</div>

					<div class="modal-footer">
						<button type="button" class="btn btn-secondary"
							data-bs-dismiss="modal">Cancel</button>
						<button type="submit" class="btn btn-success">Create
							Account</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

	<script>
		// Script for color picker remains the same
		document
				.getElementById('colorPicker')
				.addEventListener(
						'input',
						function() {
							document.getElementById('colorCode').textContent = this.value;
						});
	</script>

</body>
</html>