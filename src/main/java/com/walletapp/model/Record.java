package com.walletapp.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a financial record for a user.
 * A record can be of type "Income", "Expense", or a transfer, and may have associated labels.
 * This class also holds joined data from related tables for display purposes.
 */
public class Record {
    /** Unique ID of the record. */
    private int recordId;

    /** ID of the user who owns this record. */
    private int userId;

    /** Type of the record: Income, Expense, etc. */
    private String recordType;

    /** ID of the account associated with this record. */
    private int accountId;

    /** Category of the record (e.g., "Food", "Salary"). */
    private String category;

    /** For transfers: the source account ID. */
    private int fromAccountId;

    /** For transfers: the destination account name. */
    private String toAccountName;

    /** Amount of the transaction. */
    private BigDecimal amount;

    /** Optional note or description for the record. */
    private String note;

    /** Optional payer information. */
    private String payer;

    /** Payment type (e.g., Cash, Card, Online). */
    private String paymentType;

    /** Payment status (e.g., Cleared, Pending). */
    private String paymentStatus;

    /** Timestamp when the record was created. */
    private Timestamp recordDate;

    /** Account name for display purposes (joined from accounts table). */
    private String accountName;

    /** Account color for display purposes (joined from accounts table). */
    private String accountColor;

    /** List of labels associated with this record. */
    private List<Label> labels;

    /** Default constructor initializes the labels list. */
    public Record() {
        this.labels = new ArrayList<>();
    }

    /** @return the unique record ID */
    public int getRecordId() { return recordId; }

    /** @param recordId set the unique record ID */
    public void setRecordId(int recordId) { this.recordId = recordId; }

    /** @return the user ID owning this record */
    public int getUserId() { return userId; }

    /** @param userId set the user ID owning this record */
    public void setUserId(int userId) { this.userId = userId; }

    /** @return the type of record (Income, Expense, etc.) */
    public String getRecordType() { return recordType; }

    /** @param recordType set the type of record */
    public void setRecordType(String recordType) { this.recordType = recordType; }

    /** @return the account ID associated with this record */
    public int getAccountId() { return accountId; }

    /** @param accountId set the account ID associated with this record */
    public void setAccountId(int accountId) { this.accountId = accountId; }

    /** @return the category of the record */
    public String getCategory() { return category; }

    /** @param category set the category of the record */
    public void setCategory(String category) { this.category = category; }

    /** @return the source account ID for transfers */
    public int getFromAccountId() { return fromAccountId; }

    /** @param fromAccountId set the source account ID for transfers */
    public void setFromAccountId(int fromAccountId) { this.fromAccountId = fromAccountId; }

    /** @return the destination account name for transfers */
    public String getToAccountName() { return toAccountName; }

    /** @param toAccountName set the destination account name for transfers */
    public void setToAccountName(String toAccountName) { this.toAccountName = toAccountName; }

    /** @return the transaction amount */
    public BigDecimal getAmount() { return amount; }

    /** @param amount set the transaction amount */
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    /** @return the note or description of the record */
    public String getNote() { return note; }

    /** @param note set the note or description of the record */
    public void setNote(String note) { this.note = note; }

    /** @return the payer of the record */
    public String getPayer() { return payer; }

    /** @param payer set the payer of the record */
    public void setPayer(String payer) { this.payer = payer; }

    /** @return the payment type */
    public String getPaymentType() { return paymentType; }

    /** @param paymentType set the payment type */
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    /** @return the payment status */
    public String getPaymentStatus() { return paymentStatus; }

    /** @param paymentStatus set the payment status */
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    /** @return the timestamp when the record was created */
    public Timestamp getRecordDate() { return recordDate; }

    /** @param recordDate set the timestamp when the record was created */
    public void setRecordDate(Timestamp recordDate) { this.recordDate = recordDate; }

    /** @return the account name (joined from accounts table) */
    public String getAccountName() { return accountName; }

    /** @param accountName set the account name (joined from accounts table) */
    public void setAccountName(String accountName) { this.accountName = accountName; }

    /** @return the account color (joined from accounts table) */
    public String getAccountColor() { return accountColor; }

    /** @param accountColor set the account color (joined from accounts table) */
    public void setAccountColor(String accountColor) { this.accountColor = accountColor; }

    /** @return list of labels associated with this record */
    public List<Label> getLabels() { return labels; }

    /** @param labels set the list of labels associated with this record */
    public void setLabels(List<Label> labels) { this.labels = labels; }
}
