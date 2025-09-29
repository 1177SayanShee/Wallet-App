package com.walletapp.model;

import java.time.LocalDate;

public class Record {

    private int id;
    private double amount;
    private String label;
    private String recordType; // "Income" or "Expense"
    private String category;
    private String account;
    private String currency;
    private LocalDate date;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getRecordType() { return recordType; }
    public void setRecordType(String recordType) { this.recordType = recordType; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    // Constructor (optional, but good practice)
    public Record(int id, double amount, String label, String recordType, String category, String account, String currency, LocalDate date) {
        this.id = id;
        this.amount = amount;
        this.label = label;
        this.recordType = recordType;
        this.category = category;
        this.account = account;
        this.currency = currency;
        this.date = date;
    }

    // Default constructor for JavaBeans
    public Record() {}
}
