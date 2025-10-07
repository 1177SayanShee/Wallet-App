package com.walletapp.dao;

import com.walletapp.model.Account;
import com.walletapp.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;


/**
 * The {@code DashboardDAO} class handles all database interactions related to the
 * dashboard view. It provides summarized financial data such as total income, expense,
 * and balances across accounts for a specific user.
 *
 * <p>This class supports both global-level and account-level data aggregation,
 * including category-wise summaries, balance calculations, and dynamic date filtering.</p>
 *
 * <p>It uses {@link DBUtil} for establishing database connections.</p>
 *
 * @author Sayan
 * @version 1.0
 * @since 2025-10-07
 */
public class DashboardDAO {

	
	/**
     * Represents a container for calculated financial metrics associated with a specific account.
     * It holds total income, total expense, and balance information.
     */
	public static class AccountMetrics {
		private Account account;
		private BigDecimal totalIncome = BigDecimal.ZERO;
		private BigDecimal totalExpense = BigDecimal.ZERO;
		private BigDecimal balance = BigDecimal.ZERO;

		 /**
         * Constructs an {@code AccountMetrics} object linked to the given account.
         *
         * @param account The {@link Account} object for which metrics are calculated.
         */
		public AccountMetrics(Account account) {
			this.account = account;
		}

		 /** @return The associated {@link Account} */
		public Account getAccount() {
			return account;
		}

		 /** @return The total income recorded for this account */
		public BigDecimal getTotalIncome() {
			return totalIncome;
		}

		/** @return The total expense recorded for this account */
		public BigDecimal getTotalExpense() {
			return totalExpense;
		}

		
		/** @return The computed account balance */
		public BigDecimal getBalance() {
			return balance;
		}

		 /**
         * Sets the total income for this account.
         * @param totalIncome The total income amount.
         */
		public void setTotalIncome(BigDecimal totalIncome) {
			this.totalIncome = totalIncome;
		}

		
		 /**
         * Sets the total expense for this account.
         * @param totalExpense The total expense amount.
         */
		public void setTotalExpense(BigDecimal totalExpense) {
			this.totalExpense = totalExpense;
		}

		
		/**
         * Sets the computed balance for this account.
         * @param balance The calculated balance.
         */
		public void setBalance(BigDecimal balance) {
			this.balance = balance;
		}
	}

	/**
     * Retrieves a list of {@link AccountMetrics} for all accounts belonging to the user.
     * The results include account details, total income, total expense, and computed balance.
     *
     * @param userId   The ID of the user.
     * @param fromDate Optional filter start date.
     * @param toDate   Optional filter end date.
     * @return A list of {@link AccountMetrics} objects.
     * @throws SQLException If a database access error occurs.
     */
	public List<AccountMetrics> getAccountMetrics(int userId, Date fromDate, Date toDate) throws SQLException {
		List<AccountMetrics> result = new ArrayList<>();

		String sql = """
				    SELECT a.account_id, a.account_name, a.account_type, a.initial_amount, a.currency, a.color, a.exclude_from_statistics, a.created_at,
				           COALESCE(SUM(CASE WHEN r.record_type='Income' THEN r.amount ELSE 0 END), 0) AS total_income,
				           COALESCE(SUM(CASE WHEN r.record_type='Expense' THEN r.amount ELSE 0 END), 0) AS total_expense
				    FROM accounts a
				    LEFT JOIN records r ON a.account_id = r.account_id AND r.user_id = ? AND r.record_type IN ('Income','Expense')
				""";

		StringBuilder where = new StringBuilder();
		List<Object> params = new ArrayList<>();
		params.add(userId);

		if (fromDate != null) {
			where.append(" AND r.record_date >= ?");
			params.add(fromDate);
		}
		if (toDate != null) {
			where.append(" AND r.record_date <= ?");
			params.add(toDate);
		}

		String groupBy = " GROUP BY a.account_id ORDER BY a.account_id";
		sql += where.toString() + groupBy;

		try (Connection conn = DBUtil.getInstance().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			for (int i = 0; i < params.size(); i++) {
				Object p = params.get(i);
				if (p instanceof Date d)
					ps.setDate(i + 1, d);
				else
					ps.setObject(i + 1, p);
			}

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Account acc = new Account();
					acc.setId(rs.getInt("account_id"));
					acc.setUserId(userId);
					acc.setName(rs.getString("account_name"));
					acc.setAccountType(rs.getString("account_type"));
					acc.setInitialBalance(rs.getBigDecimal("initial_amount") == null ? BigDecimal.ZERO
							: rs.getBigDecimal("initial_amount"));
					acc.setCurrency(rs.getString("currency"));
					acc.setColor(rs.getString("color"));
					acc.setExcludeFromStats(rs.getBoolean("exclude_from_statistics"));
					Timestamp t = rs.getTimestamp("created_at");
					if (t != null)
						acc.setCreatedAt(t.toLocalDateTime());

					BigDecimal income = rs.getBigDecimal("total_income") == null ? BigDecimal.ZERO
							: rs.getBigDecimal("total_income");
					BigDecimal expense = rs.getBigDecimal("total_expense") == null ? BigDecimal.ZERO
							: rs.getBigDecimal("total_expense");
					BigDecimal balance = acc.getInitialBalance().add(income).subtract(expense);

					AccountMetrics am = new AccountMetrics(acc);
					am.setTotalIncome(income);
					am.setTotalExpense(expense);
					am.setBalance(balance);

					result.add(am);
				}
			}
		}

		return result;
	}

	/**
     * Returns category totals grouped by account and record type (Income/Expense).
     *
     * @param userId   The user ID.
     * @param fromDate Optional start date filter.
     * @param toDate   Optional end date filter.
     * @return Nested map structure: {@code Map<accountId, Map<recordType, Map<category, total>>>}
     * @throws SQLException If a database access error occurs.
     */
	public Map<Integer, Map<String, Map<String, BigDecimal>>> getCategoryTotalsForUser(int userId, Date fromDate,
			Date toDate) throws SQLException {
		Map<Integer, Map<String, Map<String, BigDecimal>>> output = new HashMap<>();

		String sql = """
				    SELECT r.account_id, r.record_type, r.category, COALESCE(SUM(r.amount),0) AS total
				    FROM records r
				    WHERE r.user_id = ? AND r.record_type IN ('Income','Expense')
				""";

		StringBuilder where = new StringBuilder();
		List<Object> params = new ArrayList<>();
		params.add(userId);

		if (fromDate != null) {
			where.append(" AND r.record_date >= ?");
			params.add(fromDate);
		}
		if (toDate != null) {
			where.append(" AND r.record_date <= ?");
			params.add(toDate);
		}

		sql += where.toString() + " GROUP BY r.account_id, r.record_type, r.category ORDER BY r.account_id";

		try (Connection conn = DBUtil.getInstance().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			for (int i = 0; i < params.size(); i++) {
				Object p = params.get(i);
				if (p instanceof Date d)
					ps.setDate(i + 1, d);
				else
					ps.setObject(i + 1, p);
			}

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int accountId = rs.getInt("account_id");
					String recordType = rs.getString("record_type");
					String category = rs.getString("category") == null ? "Others" : rs.getString("category");
					BigDecimal total = rs.getBigDecimal("total") == null ? BigDecimal.ZERO : rs.getBigDecimal("total");

					output.computeIfAbsent(accountId, k -> new HashMap<>())
							.computeIfAbsent(recordType, k -> new LinkedHashMap<>()).put(category, total);
				}
			}
		}

		return output;
	}


	/**
     * Computes the global income, expense, and net total for the user.
     *
     * @param userId   The user ID.
     * @param fromDate Optional start date.
     * @param toDate   Optional end date.
     * @return A map containing keys: "income", "expense", and "net".
     * @throws SQLException If a database error occurs.
     */
	public Map<String, BigDecimal> getSummary(int userId, Date fromDate, Date toDate) throws SQLException {
		Map<String, BigDecimal> summary = new HashMap<>();
		String sql = """
				    SELECT
				      COALESCE(SUM(CASE WHEN record_type='Income' THEN amount ELSE 0 END), 0) AS total_income,
				      COALESCE(SUM(CASE WHEN record_type='Expense' THEN amount ELSE 0 END), 0) AS total_expense
				    FROM records
				    WHERE user_id = ?
				""";

		StringBuilder where = new StringBuilder();
		List<Object> params = new ArrayList<>();
		params.add(userId);

		if (fromDate != null) {
			where.append(" AND record_date >= ?");
			params.add(fromDate);
		}
		if (toDate != null) {
			where.append(" AND record_date <= ?");
			params.add(toDate);
		}

		sql += where.toString();

		try (Connection conn = DBUtil.getInstance().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			for (int i = 0; i < params.size(); i++) {
				Object param = params.get(i);
				if (param instanceof Date d)
					ps.setDate(i + 1, d);
				else
					ps.setObject(i + 1, param);
			}

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					BigDecimal income = rs.getBigDecimal("total_income") == null ? BigDecimal.ZERO
							: rs.getBigDecimal("total_income");
					BigDecimal expense = rs.getBigDecimal("total_expense") == null ? BigDecimal.ZERO
							: rs.getBigDecimal("total_expense");
					BigDecimal net = income.subtract(expense);

					summary.put("income", income);
					summary.put("expense", expense);
					summary.put("net", net);
				}
			}
		}
		return summary;
	}

	public List<AccountMetrics> getAccountMetricsForAccount(int userId, int accountId, Date fromDate, Date toDate) throws SQLException {
	    List<AccountMetrics> result = new ArrayList<>();

	    String sql = """
	        SELECT a.account_id, a.account_name, a.account_type, a.initial_amount, a.currency, a.color, a.exclude_from_statistics, a.created_at,
	               COALESCE(SUM(CASE WHEN r.record_type='Income' THEN r.amount ELSE 0 END), 0) AS total_income,
	               COALESCE(SUM(CASE WHEN r.record_type='Expense' THEN r.amount ELSE 0 END), 0) AS total_expense
	        FROM accounts a
	        LEFT JOIN records r ON a.account_id = r.account_id AND r.user_id = ? AND r.record_type IN ('Income','Expense')
	    """;

	    StringBuilder where = new StringBuilder(" WHERE a.user_id = ? AND a.account_id = ?");
	    List<Object> params = new ArrayList<>();
	    params.add(userId); // for LEFT JOIN filter
	    params.add(userId); // for WHERE
	    params.add(accountId); // for WHERE

	    if (fromDate != null) {
	        where.append(" AND r.record_date >= ?");
	        params.add(fromDate);
	    }
	    if (toDate != null) {
	        where.append(" AND r.record_date <= ?");
	        params.add(toDate);
	    }

	    sql += where.toString() + " GROUP BY a.account_id ORDER BY a.account_id";

	    try (Connection conn = DBUtil.getInstance().getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        for (int i = 0; i < params.size(); i++) {
	            Object p = params.get(i);
	            if (p instanceof Date d) ps.setDate(i + 1, d);
	            else ps.setObject(i + 1, p);
	        }

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                Account acc = new Account();
	                acc.setId(rs.getInt("account_id"));
	                acc.setUserId(userId);
	                acc.setName(rs.getString("account_name"));
	                acc.setAccountType(rs.getString("account_type"));
	                acc.setInitialBalance(rs.getBigDecimal("initial_amount") == null ? BigDecimal.ZERO : rs.getBigDecimal("initial_amount"));
	                acc.setCurrency(rs.getString("currency"));
	                acc.setColor(rs.getString("color"));
	                acc.setExcludeFromStats(rs.getBoolean("exclude_from_statistics"));
	                Timestamp t = rs.getTimestamp("created_at");
	                if (t != null) acc.setCreatedAt(t.toLocalDateTime());

	                BigDecimal income = rs.getBigDecimal("total_income") == null ? BigDecimal.ZERO : rs.getBigDecimal("total_income");
	                BigDecimal expense = rs.getBigDecimal("total_expense") == null ? BigDecimal.ZERO : rs.getBigDecimal("total_expense");
	                BigDecimal balance = acc.getInitialBalance().add(income).subtract(expense);

	                AccountMetrics am = new AccountMetrics(acc);
	                am.setTotalIncome(income);
	                am.setTotalExpense(expense);
	                am.setBalance(balance);

	                result.add(am);
	            }
	        }
	    }

	    return result;
	}

	
	public Map<Integer, Map<String, Map<String, BigDecimal>>> getCategoryTotalsForAccount(int userId, int accountId, Date fromDate, Date toDate) throws SQLException {
	    Map<Integer, Map<String, Map<String, BigDecimal>>> output = new HashMap<>();

	    String sql = """
	        SELECT r.account_id, r.record_type, r.category, COALESCE(SUM(r.amount),0) AS total
	        FROM records r
	        WHERE r.user_id = ? AND r.account_id = ? AND r.record_type IN ('Income','Expense')
	    """;

	    List<Object> params = new ArrayList<>();
	    params.add(userId);
	    params.add(accountId);

	    if (fromDate != null) {
	        sql += " AND r.record_date >= ?";
	        params.add(fromDate);
	    }
	    if (toDate != null) {
	        sql += " AND r.record_date <= ?";
	        params.add(toDate);
	    }

	    sql += " GROUP BY r.account_id, r.record_type, r.category ORDER BY r.account_id";

	    try (Connection conn = DBUtil.getInstance().getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        for (int i = 0; i < params.size(); i++) {
	            Object p = params.get(i);
	            if (p instanceof Date d) ps.setDate(i + 1, d);
	            else ps.setObject(i + 1, p);
	        }

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                int accId = rs.getInt("account_id");
	                String recordType = rs.getString("record_type");
	                String category = rs.getString("category") == null ? "Others" : rs.getString("category");
	                BigDecimal total = rs.getBigDecimal("total") == null ? BigDecimal.ZERO : rs.getBigDecimal("total");

	                output.computeIfAbsent(accId, k -> new HashMap<>())
	                      .computeIfAbsent(recordType, k -> new LinkedHashMap<>())
	                      .put(category, total);
	            }
	        }
	    }

	    return output;
	}


	public Map<String, BigDecimal> getSummaryForAccount(int userId, int accountId, Date fromDate, Date toDate) throws SQLException {
	    Map<String, BigDecimal> summary = new HashMap<>();

	    String sql = """
	        SELECT
	            COALESCE(SUM(CASE WHEN record_type='Income' THEN amount ELSE 0 END),0) AS income,
	            COALESCE(SUM(CASE WHEN record_type='Expense' THEN amount ELSE 0 END),0) AS expense
	        FROM records
	        WHERE user_id = ? AND account_id = ?
	    """;

	    List<Object> params = new ArrayList<>();
	    params.add(userId);
	    params.add(accountId);

	    if (fromDate != null) {
	        sql += " AND record_date >= ?";
	        params.add(fromDate);
	    }
	    if (toDate != null) {
	        sql += " AND record_date <= ?";
	        params.add(toDate);
	    }

	    try (Connection conn = DBUtil.getInstance().getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        for (int i = 0; i < params.size(); i++) {
	            Object p = params.get(i);
	            if (p instanceof Date d) ps.setDate(i + 1, d);
	            else ps.setObject(i + 1, p);
	        }

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                BigDecimal income = rs.getBigDecimal("income") == null ? BigDecimal.ZERO : rs.getBigDecimal("income");
	                BigDecimal expense = rs.getBigDecimal("expense") == null ? BigDecimal.ZERO : rs.getBigDecimal("expense");
	                BigDecimal net = income.subtract(expense);

	                summary.put("income", income);
	                summary.put("expense", expense);
	                summary.put("net", net);
	            }
	        }
	    }

	    return summary;
	}


}

