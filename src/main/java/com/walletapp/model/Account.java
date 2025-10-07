package com.walletapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a financial account of a user.
 * Stores information such as account name, type, initial balance, currency, color, 
 * whether it should be excluded from statistics, and creation timestamp.
 */
public class Account {
    /** Unique identifier for the account. */
    private Integer id;

    /** ID of the user who owns this account. */
    private Integer userId;

    /** Name of the account (e.g., "Savings", "Wallet"). */
    private String name;

    /** Type of the account (e.g., "Bank", "Cash"). */
    private String accountType;

    /** The initial balance set when the account is created. */
    private BigDecimal initialBalance;

    /** Currency used for the account (e.g., "USD", "INR"). */
    private String currency;

    /** Color code for display purposes (e.g., for charts or UI). */
    private String color;

    /** Whether this account should be excluded from statistics and reports. */
    private Boolean excludeFromStats;

    /** Timestamp of when the account was created. */
    private LocalDateTime createdAt;

    /** Default no-argument constructor. */
    public Account() {}

    /**
     * Parameterized constructor to create an account with all details.
     * 
     * @param id Unique identifier for the account.
     * @param userId ID of the user owning this account.
     * @param name Name of the account.
     * @param accountType Type of the account.
     * @param initialBalance Initial balance of the account.
     * @param currency Currency used for this account.
     * @param color Color code for UI representation.
     * @param excludeFromStats Whether to exclude from statistics.
     * @param createdAt Account creation timestamp.
     */
    public Account(Integer id, Integer userId, String name, String accountType,
                   BigDecimal initialBalance, String currency, String color,
                   Boolean excludeFromStats, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.accountType = accountType;
        this.initialBalance = initialBalance;
        this.currency = currency;
        this.color = color;
        this.excludeFromStats = excludeFromStats;
        this.createdAt = createdAt;
    }

    /** @return the unique account ID */
    public Integer getId() { return id; }

    /** @param id set the unique account ID */
    public void setId(Integer id) { this.id = id; }

    /** @return the user ID owning this account */
    public Integer getUserId() { return userId; }

    /** @param userId set the user ID owning this account */
    public void setUserId(Integer userId) { this.userId = userId; }

    /** @return the name of the account */
    public String getName() { return name; }

    /** @param name set the name of the account */
    public void setName(String name) { this.name = name; }

    /** @return the type of the account */
    public String getAccountType() { return accountType; }

    /** @param accountType set the type of the account */
    public void setAccountType(String accountType) { this.accountType = accountType; }

    /** @return the initial balance of the account */
    public BigDecimal getInitialBalance() { return initialBalance; }

    /** @param initialBalance set the initial balance of the account */
    public void setInitialBalance(BigDecimal initialBalance) { this.initialBalance = initialBalance; }

    /** @return the currency of the account */
    public String getCurrency() { return currency; }

    /** @param currency set the currency of the account */
    public void setCurrency(String currency) { this.currency = currency; }

    /** @return the color code for UI display */
    public String getColor() { return color; }

    /** @param color set the color code for UI display */
    public void setColor(String color) { this.color = color; }

    /** @return true if excluded from statistics, false otherwise */
    public Boolean getExcludeFromStats() { return excludeFromStats; }

    /** @param excludeFromStats set whether to exclude this account from statistics */
    public void setExcludeFromStats(Boolean excludeFromStats) { this.excludeFromStats = excludeFromStats; }

    /** @return the creation timestamp of the account */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /** @param createdAt set the creation timestamp of the account */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
