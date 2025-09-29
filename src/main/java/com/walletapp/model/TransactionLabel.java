package com.walletapp.model;

public class TransactionLabel {
    private Integer transactionId;
    private Integer labelId;

    public TransactionLabel() {}

    public TransactionLabel(Integer transactionId, Integer labelId) {
        this.transactionId = transactionId;
        this.labelId = labelId;
    }

    public Integer getTransactionId() { return transactionId; }
    public void setTransactionId(Integer transactionId) { this.transactionId = transactionId; }

    public Integer getLabelId() { return labelId; }
    public void setLabelId(Integer labelId) { this.labelId = labelId; }
}
