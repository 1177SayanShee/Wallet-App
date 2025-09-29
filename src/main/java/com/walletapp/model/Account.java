package com.walletapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account {
    private Integer id;
    private Integer userId;
    private String name;
    private String accountType;
    private BigDecimal initialBalance;
    private String currency;
    private String color;
    private Boolean excludeFromStats;
    private LocalDateTime createdAt;

    public Account() {}

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

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public BigDecimal getInitialBalance() { return initialBalance; }
    public void setInitialBalance(BigDecimal initialBalance) { this.initialBalance = initialBalance; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Boolean getExcludeFromStats() { return excludeFromStats; }
    public void setExcludeFromStats(Boolean excludeFromStats) { this.excludeFromStats = excludeFromStats; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
