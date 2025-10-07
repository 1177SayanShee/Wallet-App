package com.walletapp.model;

/**
 * Represents the association between a Record and a Label.
 * Each instance corresponds to a row in the 'record_labels' junction table.
 */
public class RecordLabel {
    
    /** Unique ID of this record-label association. */
    private int recordLabelId;

    /** ID of the associated record. */
    private int recordId;

    /** ID of the associated label. */
    private int labelId;

    /** 
     * Default constructor.
     * Initializes an empty RecordLabel instance.
     */
    public RecordLabel() {}

    /**
     * Parameterized constructor.
     * @param recordLabelId Unique ID of this record-label association
     * @param recordId ID of the associated record
     * @param labelId ID of the associated label
     */
    public RecordLabel(int recordLabelId, int recordId, int labelId) {
        this.recordLabelId = recordLabelId;
        this.recordId = recordId;
        this.labelId = labelId;
    }

    /** @return the unique record-label association ID */
    public int getRecordLabelId() {
        return recordLabelId;
    }

    /** @param recordLabelId set the unique record-label association ID */
    public void setRecordLabelId(int recordLabelId) {
        this.recordLabelId = recordLabelId;
    }

    /** @return the associated record ID */
    public int getRecordId() {
        return recordId;
    }

    /** @param recordId set the associated record ID */
    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    /** @return the associated label ID */
    public int getLabelId() {
        return labelId;
    }

    /** @param labelId set the associated label ID */
    public void setLabelId(int labelId) {
        this.labelId = labelId;
    }
}
