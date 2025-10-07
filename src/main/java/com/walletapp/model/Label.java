package com.walletapp.model;

/**
 * Represents a label/tag that can be assigned to a record.
 * Each label belongs to a user and can have a name and a color for UI display.
 */
public class Label {
    /** Unique identifier for the label. */
    private int labelId;

    /** ID of the user who owns this label. */
    private int userId;

    /** Name of the label (e.g., "Food", "Salary"). */
    private String labelName;

    /** Color code for display purposes (e.g., in charts or UI). */
    private String color;

    /** Default no-argument constructor. */
    public Label() {}

    /**
     * Constructor to create a label without specifying the user ID.
     * Useful for situations where user context is already known.
     * 
     * @param labelId Unique identifier for the label.
     * @param labelName Name of the label.
     * @param color Color code of the label.
     */
    public Label(int labelId, String labelName, String color) {
        this.labelId = labelId;
        this.labelName = labelName;
        this.color = color;
    }

    /**
     * Constructor to create a label with full details.
     * 
     * @param labelId Unique identifier for the label.
     * @param userId ID of the user owning this label.
     * @param labelName Name of the label.
     * @param color Color code of the label.
     */
    public Label(int labelId, int userId, String labelName, String color) {
        this.labelId = labelId;
        this.userId = userId;
        this.labelName = labelName;
        this.color = color;
    }

    /** @return the unique label ID */
    public int getLabelId() { return labelId; }

    /** @param labelId set the unique label ID */
    public void setLabelId(int labelId) { this.labelId = labelId; }

    /** @return the user ID owning this label */
    public int getUserId() { return userId; }

    /** @param userId set the user ID owning this label */
    public void setUserId(int userId) { this.userId = userId; }

    /** @return the name of the label */
    public String getLabelName() { return labelName; }

    /** @param labelName set the name of the label */
    public void setLabelName(String labelName) { this.labelName = labelName; }

    /** @return the color code of the label */
    public String getColor() { return color; }

    /** @param color set the color code of the label */
    public void setColor(String color) { this.color = color; }
}
