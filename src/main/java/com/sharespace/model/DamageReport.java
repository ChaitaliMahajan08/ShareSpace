package com.sharespace.model;

import java.time.LocalDateTime;

/**
 * Represents a damage report filed for a resource in the ShareSpace system.
 * Severity levels: MINOR, MODERATE, SEVERE
 */
public class DamageReport {

    private int id;
    private int transactionId;
    private int resourceId;
    private String resourceName;
    private String damageDescription;
    private String severity; // MINOR, MODERATE, SEVERE
    private int reportedByManagerId;
    private LocalDateTime reportedAt;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DamageReport() {
        this.reportedAt = LocalDateTime.now();
    }

    public DamageReport(int id, int transactionId, int resourceId, String resourceName,
                        String damageDescription, String severity,
                        int reportedByManagerId, LocalDateTime reportedAt) {
        this.id = id;
        this.transactionId = transactionId;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.damageDescription = damageDescription;
        this.severity = severity;
        this.reportedByManagerId = reportedByManagerId;
        this.reportedAt = reportedAt;
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getDamageDescription() {
        return damageDescription;
    }

    public void setDamageDescription(String damageDescription) {
        this.damageDescription = damageDescription;
    }

    public String getSeverity() {
        return severity;
    }

    /**
     * Sets the severity of the damage. Must be MINOR, MODERATE, or SEVERE.
     */
    public void setSeverity(String severity) {
        if (severity == null) {
            throw new IllegalArgumentException("Severity must not be null.");
        }
        String upper = severity.trim().toUpperCase();
        if (!upper.equals("MINOR") && !upper.equals("MODERATE") && !upper.equals("SEVERE")) {
            throw new IllegalArgumentException(
                    "Severity must be MINOR, MODERATE, or SEVERE. Got: " + severity);
        }
        this.severity = upper;
    }

    public int getReportedByManagerId() {
        return reportedByManagerId;
    }

    public void setReportedByManagerId(int reportedByManagerId) {
        this.reportedByManagerId = reportedByManagerId;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(LocalDateTime reportedAt) {
        this.reportedAt = reportedAt;
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "DamageReport{id=" + id + ", transactionId=" + transactionId
                + ", resourceId=" + resourceId + ", resourceName='" + resourceName + "'"
                + ", severity='" + severity + "'"
                + ", reportedByManagerId=" + reportedByManagerId
                + ", reportedAt=" + reportedAt + "}";
    }
}
