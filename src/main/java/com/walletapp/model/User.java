package com.walletapp.model;

import java.util.Date;

public class User {
    private int id;
    private String email;
    private String passwordHash;
    private String tempPasswordHash;
    private Date tempPasswordExpiry;
    private boolean mustChangePassword;

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getTempPasswordHash() { return tempPasswordHash; }
    public void setTempPasswordHash(String tempPasswordHash) { this.tempPasswordHash = tempPasswordHash; }
    public Date getTempPasswordExpiry() { return tempPasswordExpiry; }
    public void setTempPasswordExpiry(Date tempPasswordExpiry) { this.tempPasswordExpiry = tempPasswordExpiry; }
    public boolean isMustChangePassword() { return mustChangePassword; }
    public void setMustChangePassword(boolean mustChangePassword) { this.mustChangePassword = mustChangePassword; }
}
