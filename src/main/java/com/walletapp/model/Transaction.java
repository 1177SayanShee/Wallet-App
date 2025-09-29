package com.walletapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.walletapp.model.enums.TransactionType;

public class Transaction {
    private Integer id;
    private Integer userId;
    private Integer accountId;
    private Integer categoryId; // nullable
    private TransactionType transactionType;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String note;
    private Integer toAccountId; // nullable
    private LocalDateTime createdAt;

    public Transaction() {}

    public Transaction(Integer id, Integer userId, Integer accountId, Integer categoryId,
                       TransactionType transactionType, BigDecimal amount,
                       LocalDateTime transactionDate, String note,
                       Integer toAccountId, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.note = note;
        this.toAccountId = toAccountId;
        this.createdAt = createdAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getAccountId() { return accountId; }
    public void setAccountId(Integer accountId) { this.accountId = accountId; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Integer getToAccountId() { return toAccountId; }
    public void setToAccountId(Integer toAccountId) { this.toAccountId = toAccountId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
