<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page
	import="java.util.*, java.math.BigDecimal, java.text.NumberFormat, java.util.Locale, com.walletapp.dao.DashboardDAO.AccountMetrics, com.walletapp.model.Account"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Dashboard</title>

<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link
	href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap"
	rel="stylesheet">

<style>
:root {
	--primary-green: #28a745;
	--bg: #f4f6f8;
	--card-border: #e9ecef;
}

body {
	font-family: 'Inter', sans-serif;
	background: var(--bg);
	margin: 0;
	color: #212529;
}

.main-wrapper {
	display: flex;
	min-height: 100vh;
}

/* Sidebar */
.sidebar {
	width: 260px;
	background: #fff;
	padding: 20px;
	border-right: 1px solid var(--card-border);
	position: fixed;
	top: 0;
	left: 0;
	height: 100vh;
	box-sizing: border-box;
}

.sidebar .logo {
	width: 44px;
	height: 44px;
	background: var(--primary-green);
	border-radius: 10px;
	margin-bottom: 18px;
}

.sidebar h2 {
	font-size: 20px;
	margin: 0 0 12px 0;
}

/* filter in sidebar */
.sidebar .filter-form {
	margin-top: 20px;
}

/* Main */
.main-content {
	margin-left: 260px;
	width: calc(100% - 260px);
	padding: 24px;
	box-sizing: border-box;
}

/* Header/nav */
.header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 16px;
}

.main-nav a {
	color: #6c757d;
	text-decoration: none;
	padding: 8px 12px;
	border-radius: 8px;
	margin-right: 6px;
	display: inline-block;
}

.main-nav a.active, .main-nav a:hover {
	background: #e9f5ec;
	color: var(--primary-green);
}

/* cards */
.card-dashboard {
	background: #fff;
	border-radius: 14px;
	padding: 18px;
	border: 1px solid var(--card-border);
	box-shadow: 0 10px 30px rgba(16, 24, 40, 0.06);
	margin-bottom: 20px;
	transition: transform .12s ease, box-shadow .12s ease;
}

.card-dashboard:hover {
	transform: translateY(-6px);
	box-shadow: 0 18px 44px rgba(16, 24, 40, 0.08);
}

/* header gradient */
.card-header-gradient {
	background: linear-gradient(90deg, rgba(40, 167, 69, 0.12),
		rgba(40, 167, 69, 0.04));
	padding: 10px;
	border-radius: 10px;
	margin-bottom: 12px;
}

/* account row */
.account-row {
	display: flex;
	align-items: center;
	gap: 12px;
}

.account-color {
	width: 44px;
	height: 44px;
	border-radius: 10px;
	flex-shrink: 0;
}

/* metrics */
.metric {
	font-weight: 700;
	font-size: 1.05rem;
}

.metric.small {
	font-weight: 600;
	font-size: 0.95rem;
	color: #6c757d;
}

/* negative */
.balance-negative {
	color: #c82333 !important;
}

/* category table */
.category-table th, .category-table td {
	vertical-align: middle;
}

/* charts */
.chart-wrap {
	display: flex;
	gap: 16px;
	flex-wrap: wrap;
	margin-top: 12px;
	justify-content: center;
}

.chart-box {
	width: 280px;
	padding: 10px;
	border-radius: 10px;
	border: 1px solid #f1f1f1;
	background: #fff;
	text-align: center;
}

@media ( max-width : 900px) {
	.chart-box {
		width: 100%;
	}
	.account-row {
		gap: 8px;
	}
}
</style>
</head>
<body>
	<div class="main-wrapper">
		<!-- Side bar -->
		<aside class="sidebar">
			<div class="logo"></div>
			<h2>Dashboard</h2>

			<!-- Filter moved to sidebar -->
			<div class="filter-form">
				<form method="get" action="DashboardServlet">
					<!-- Account Dropdown Filter -->
					<div class="mb-3">
						<label class="form-label">Account</label> <select name="accountId"
							class="form-select">
							<option value="">All Accounts</option>
							<%
							List<Account> accounts = (List<Account>) request.getAttribute("accounts");
							Integer selectedAccountId = (Integer) request.getAttribute("selectedAccountId");
							if (accounts != null) {
								for (Account acc : accounts) {
									String selected = (selectedAccountId != null && selectedAccountId == acc.getId()) ? "selected" : "";
							%>
							<option value="<%=acc.getId()%>" <%=selected%>><%=acc.getName()%></option>
							<%
							}
							}
							%>
						</select>
					</div>

					<!-- Date Filters -->
					<div class="mb-3">
						<label class="form-label">From</label> <input type="date"
							class="form-control" name="fromDate"
							value="<%=request.getAttribute("fromDate") != null ? request.getAttribute("fromDate") : ""%>">
					</div>
					<div class="mb-3">
						<label class="form-label">To</label> <input type="date"
							class="form-control" name="toDate"
							value="<%=request.getAttribute("toDate") != null ? request.getAttribute("toDate") : ""%>">
					</div>
					<div>
						<button class="btn btn-success w-100" type="submit">Filter</button>
					</div>
				</form>
			</div>

		</aside>

		<main class="main-content">

			<header class="header">
				<nav class="main-nav">
					<a href="#" class="active">Dashboard</a> 
					<a href="accounts.jsp">Accounts</a>
					<a href="RecordServlet">Records</a> 
				</nav>
				
				<!--  -->
				<div class="header-actions d-flex align-items-center">
                    <!-- <button class="btn btn-success me-3">+ Record</button> -->
                    
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

			<!-- Summary -->
			<div class="card-dashboard">
				<div class="card-header-gradient text-center">
					<h4 style="margin: 0">Summary</h4>
				</div>

				<div class="row text-center">
					<div class="col-md-4">
						<div class="metric small">Total Income</div>
						<div class="metric">
							<%
							Map<String, BigDecimal> summary = (Map<String, BigDecimal>) request.getAttribute("summary");
							if (summary != null) {
								BigDecimal income = summary.getOrDefault("income", BigDecimal.ZERO);
								out.print(NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(income));
							} else {
								out.print("-");
							}
							%>
						</div>
					</div>

					<div class="col-md-4">
						<div class="metric small">Total Expense</div>
						<div class="metric">
							<%
							if (summary != null) {
								BigDecimal expense = summary.getOrDefault("expense", BigDecimal.ZERO);
								out.print(NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(expense));
							} else {
								out.print("-");
							}
							%>
						</div>
					</div>

					<div class="col-md-4">
						<div class="metric small">Net</div>
						<div class="metric">
							<%
							if (summary != null) {
								BigDecimal net = summary.getOrDefault("net", BigDecimal.ZERO);
								out.print(NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(net));
							} else {
								out.print("-");
							}
							%>
						</div>
					</div>
				</div>
			</div>

			<!-- Accounts & Charts -->
			<div class="accounts-area">
				<%
				List<AccountMetrics> accountMetrics = (List<AccountMetrics>) request.getAttribute("accountMetrics");
				Map<Integer, Map<String, Map<String, BigDecimal>>> categoryTotals = (Map<Integer, Map<String, Map<String, BigDecimal>>>) request
						.getAttribute("categoryTotals");

				if (accountMetrics != null && !accountMetrics.isEmpty()) {
					for (AccountMetrics am : accountMetrics) {
						com.walletapp.model.Account acc = am.getAccount();
						int accId = acc.getId();
						BigDecimal inc = am.getTotalIncome() != null ? am.getTotalIncome() : BigDecimal.ZERO;
						BigDecimal exp = am.getTotalExpense() != null ? am.getTotalExpense() : BigDecimal.ZERO;
						BigDecimal bal = am.getBalance() != null ? am.getBalance() : BigDecimal.ZERO;

						// currency formatter per account
						NumberFormat nf;
						String cur = acc.getCurrency();
						if ("USD".equalsIgnoreCase(cur))
					nf = NumberFormat.getCurrencyInstance(Locale.US);
						else if ("EUR".equalsIgnoreCase(cur))
					nf = NumberFormat.getCurrencyInstance(Locale.GERMANY);
						else if ("GBP".equalsIgnoreCase(cur))
					nf = NumberFormat.getCurrencyInstance(Locale.UK);
						else
					nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

						Map<String, Map<String, BigDecimal>> perAcc = (categoryTotals != null) ? categoryTotals.get(accId) : null;
						Map<String, BigDecimal> incomeCats = (perAcc != null && perAcc.get("Income") != null)
						? perAcc.get("Income")
						: new LinkedHashMap<>();
						Map<String, BigDecimal> expenseCats = (perAcc != null && perAcc.get("Expense") != null)
						? perAcc.get("Expense")
						: new LinkedHashMap<>();
				%>

				<div class="card-dashboard">
					<div
						class="card-header-gradient d-flex justify-content-between align-items-center">
						<div class="account-row">
							<div class="account-color"
								style="background-color: <%=acc.getColor() != null ? acc.getColor() : "#dddddd"%>"></div>
							<div>
								<div style="font-weight: 700; font-size: 1rem;"><%=acc.getName()%></div>
								<div style="color: #6c757d; font-size: 0.9rem;"><%=acc.getAccountType()%></div>
							</div>
						</div>

						<div class="text-end">
							<div class="metric small">Balance</div>
							<div
								class="metric <%=bal.compareTo(BigDecimal.ZERO) < 0 ? "balance-negative" : ""%>">
								<%
								if (bal.compareTo(BigDecimal.ZERO) < 0) {
									out.print("-" + nf.format(bal.abs()));
								} else {
									out.print(nf.format(bal));
								}
								%>
							</div>
						</div>
					</div>

					<div class="row mt-3">
						<div class="col-md-6">
							<div class="px-2">
								<div class="d-flex justify-content-between mb-2">
									<div class="metric small">Total Income</div>
									<div class="metric"><%=nf.format(inc)%></div>
								</div>

								<!-- Income categories -->
								<table class="table table-sm category-table">
									<thead>
										<tr>
											<th>Category</th>
											<th class="text-end">Amount</th>
										</tr>
									</thead>
									<tbody>
										<%
										if (incomeCats == null || incomeCats.isEmpty()) {
										%>
										<tr>
											<td colspan="2" class="text-center text-muted">No income
												categories</td>
										</tr>
										<%
										} else {
										for (Map.Entry<String, BigDecimal> e : incomeCats.entrySet()) {
										%>
										<tr>
											<td><%=e.getKey()%></td>
											<td class="text-end"><%=nf.format(e.getValue())%></td>
										</tr>
										<%
										}
										}
										%>
									</tbody>
								</table>

							</div>
						</div>

						<div class="col-md-6">
							<div class="px-2">
								<div class="d-flex justify-content-between mb-2">
									<div class="metric small">Total Expense</div>
									<div class="metric"><%=nf.format(exp)%></div>
								</div>

								<!-- Expense categories -->
								<table class="table table-sm category-table">
									<thead>
										<tr>
											<th>Category</th>
											<th class="text-end">Amount</th>
										</tr>
									</thead>
									<tbody>
										<%
										if (expenseCats == null || expenseCats.isEmpty()) {
										%>
										<tr>
											<td colspan="2" class="text-center text-muted">No
												expense categories</td>
										</tr>
										<%
										} else {
										for (Map.Entry<String, BigDecimal> e : expenseCats.entrySet()) {
										%>
										<tr>
											<td><%=e.getKey()%></td>
											<td class="text-end"><%=nf.format(e.getValue())%></td>
										</tr>
										<%
										}
										}
										%>
									</tbody>
								</table>

							</div>
						</div>
					</div>

					<!-- Chart section -->
					<div class="mt-3 chart-wrap">
						<div class="chart-box">
							<div style="font-weight: 600; margin-bottom: 8px">Income by
								Category</div>
							<canvas id="incomeChart-<%=accId%>"></canvas>
						</div>

						<div class="chart-box">
							<div style="font-weight: 600; margin-bottom: 8px">Expense
								by Category</div>
							<canvas id="expenseChart-<%=accId%>"></canvas>
						</div>

						<div class="chart-box">
							<div style="font-weight: 600; margin-bottom: 8px">Income vs
								Expense (Bar)</div>
							<canvas id="barChart-<%=accId%>"></canvas>
						</div>
					</div>

					<!-- Inline JS data -->
					<script>
    window.accountData = window.accountData || {};
    window.accountData[<%=accId%>] = {
      income: {
        labels: [<%int i = 0;
for (String k : incomeCats.keySet()) {%>'<%=k%>'<%=(++i < incomeCats.size()) ? "," : ""%><%}%>],
        values: [<%i = 0;
for (BigDecimal v : incomeCats.values()) {%><%=v%><%=(++i < incomeCats.size()) ? "," : ""%><%}%>]
      },
      expense: {
        labels: [<%i = 0;
for (String k : expenseCats.keySet()) {%>'<%=k%>'<%=(++i < expenseCats.size()) ? "," : ""%><%}%>],
        values: [<%i = 0;
for (BigDecimal v : expenseCats.values()) {%><%=v%><%=(++i < expenseCats.size()) ? "," : ""%><%}%>]
      }
    };
  </script>
				</div>
				<%
				} // loop
				} else {
				%>
				<div class="card-dashboard text-center">
					<p>No accounts found. Add one to get started.</p>
				</div>
				<%
				}
				%>
			</div>
		</main>
	</div>

	<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
	<script>
function palette(i) {
	  const p = ['#4dc9f6','#f67019','#f53794','#537bc4','#acc236','#166a8f','#00a950','#58595b','#8549ba'];
	  return p[i % p.length];
	}
	function renderCharts() {
	  if (!window.accountData) return;
	  Object.keys(window.accountData).forEach(accId => {
	    const acc = window.accountData[accId];
	    const inc = acc.income, exp = acc.expense;

	    const incCtx = document.getElementById('incomeChart-'+accId)?.getContext('2d');
	    if (incCtx && inc.labels.length)
	      new Chart(incCtx,{type:'pie',data:{labels:inc.labels,datasets:[{data:inc.values,backgroundColor:inc.labels.map((_,i)=>palette(i))}]},options:{plugins:{legend:{position:'bottom'}}}});

	    const expCtx = document.getElementById('expenseChart-'+accId)?.getContext('2d');
	    if (expCtx && exp.labels.length)
	      new Chart(expCtx,{type:'pie',data:{labels:exp.labels,datasets:[{data:exp.values,backgroundColor:exp.labels.map((_,i)=>palette(i+3))}]},options:{plugins:{legend:{position:'bottom'}}}});

	    // Bar chart combining income & expense categories
	    const barCtx = document.getElementById('barChart-'+accId)?.getContext('2d');
	    if (barCtx) {
	      const allCats = Array.from(new Set([...inc.labels, ...exp.labels]));
	      const incMap = Object.fromEntries(inc.labels.map((l,i)=>[l,inc.values[i]]));
	      const expMap = Object.fromEntries(exp.labels.map((l,i)=>[l,exp.values[i]]));
	      const incVals = allCats.map(l=>incMap[l]||0);
	      const expVals = allCats.map(l=>expMap[l]||0);

	      new Chart(barCtx,{
	        type:'bar',
	        data:{
	          labels:allCats,
	          datasets:[
	            {label:'Income',data:incVals,backgroundColor:'#28a745'},
	            {label:'Expense',data:expVals,backgroundColor:'#dc3545'}
	          ]
	        },
	        options:{
	          responsive:true,
	          plugins:{legend:{position:'bottom'}},
	          scales:{y:{beginAtZero:true}}
	        }
	      });
	    }
	  });
	}
	document.addEventListener('DOMContentLoaded', renderCharts);
</script>
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>