package com.walletapp.model;

import java.util.Date;

/**
 * Represents a user in the wallet application.
 * Contains authentication and temporary password information.
 */
public class User {

    /** Unique ID of the user. */
    private int id;

    /** Email of the user, used for login. */
    private String email;

    /** Hashed password of the user. */
    private String passwordHash;

    /** Hashed temporary password for password reset flows. */
    private String tempPasswordHash;

    /** Expiry date/time of the temporary password. */
    private Date tempPasswordExpiry;

    /** Flag indicating if the user must change their password on next login. */
    private boolean mustChangePassword;

    /** @return the user's unique ID */
    public int getId() {
        return id;
    }

    /** @param id set the user's unique ID */
    public void setId(int id) {
        this.id = id;
    }

    /** @return the user's email */
    public String getEmail() {
        return email;
    }

    /** @param email set the user's email */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return the hashed password of the user */
    public String getPasswordHash() {
        return passwordHash;
    }

    /** @param passwordHash set the hashed password of the user */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /** @return the hashed temporary password */
    public String getTempPasswordHash() {
        return tempPasswordHash;
    }

    /** @param tempPasswordHash set the hashed temporary password */
    public void setTempPasswordHash(String tempPasswordHash) {
        this.tempPasswordHash = tempPasswordHash;
    }

    /** @return the expiry date of the temporary password */
    public Date getTempPasswordExpiry() {
        return tempPasswordExpiry;
    }

    /** @param tempPasswordExpiry set the expiry date of the temporary password */
    public void setTempPasswordExpiry(Date tempPasswordExpiry) {
        this.tempPasswordExpiry = tempPasswordExpiry;
    }

    /** @return true if the user must change their password on next login */
    public boolean isMustChangePassword() {
        return mustChangePassword;
    }

    /** @param mustChangePassword set whether the user must change their password on next login */
    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }
}
