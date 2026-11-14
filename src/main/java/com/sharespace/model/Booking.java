package com.sharespace.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a booking made by a student for a resource in the ShareSpace system.
 */
public class Booking {

    // Valid statuses: PENDING, APPROVED, REJECTED, ISSUED, RETURNED, CANCELLED
    private int id;
    private int studentId;
    private String studentName;
    private int resourceId;
    private String resourceName;
    private int quantityRequested;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String remarks;
    private LocalDateTime createdAt;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Booking() {
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public Booking(int id, int studentId, String studentName, int resourceId,
                   String resourceName, int quantityRequested, LocalDate startDate,
                   LocalDate endDate, String status, String remarks, LocalDateTime createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.quantityRequested = quantityRequested;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.remarks = remarks;
        this.createdAt = createdAt;
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

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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

    public int getQuantityRequested() {
        return quantityRequested;
    }

    public void setQuantityRequested(int quantityRequested) {
        this.quantityRequested = quantityRequested;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // -------------------------------------------------------------------------
    // Business methods
    // -------------------------------------------------------------------------

    /**
     * Returns true if the end date is strictly after the start date.
     */
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) return false;
        return endDate.isAfter(startDate);
    }

    /**
     * Returns true if this booking can still be cancelled (status is PENDING).
     */
    public boolean isCancellable() {
        return "PENDING".equalsIgnoreCase(status);
    }

    /**
     * Builds a full booking receipt using StringBuilder.
     */
    public String generateBookingReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================\n");
        sb.append("             BOOKING RECEIPT                \n");
        sb.append("============================================\n");
        sb.append("Booking ID         : ").append(id).append("\n");
        sb.append("Student ID         : ").append(studentId).append("\n");
        sb.append("Student Name       : ").append(studentName).append("\n");
        sb.append("Resource ID        : ").append(resourceId).append("\n");
        sb.append("Resource Name      : ").append(resourceName).append("\n");
        sb.append("Quantity Requested : ").append(quantityRequested).append("\n");
        sb.append("Start Date         : ").append(startDate).append("\n");
        sb.append("End Date           : ").append(endDate).append("\n");
        sb.append("Status             : ").append(status).append("\n");
        sb.append("Remarks            : ").append(remarks != null ? remarks : "N/A").append("\n");
        sb.append("Created At         : ").append(createdAt).append("\n");
        sb.append("Valid Date Range   : ").append(isValidDateRange() ? "Yes" : "No").append("\n");
        sb.append("Cancellable        : ").append(isCancellable() ? "Yes" : "No").append("\n");
        sb.append("============================================\n");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Booking{id=" + id + ", studentId=" + studentId
                + ", studentName='" + studentName + "', resourceId=" + resourceId
                + ", resourceName='" + resourceName + "', quantity=" + quantityRequested
                + ", startDate=" + startDate + ", endDate=" + endDate
                + ", status='" + status + "'}";
    }
}
