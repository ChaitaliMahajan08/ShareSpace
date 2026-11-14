package com.sharespace.model;

import java.time.LocalDateTime;

/**
 * Represents a transaction record (issue/return) in the ShareSpace system.
 */
public class Transaction {

    private int id;
    private int bookingId;
    private int managerId;
    private String managerName;
    private LocalDateTime issuedAt;
    private LocalDateTime returnedAt;
    private String conditionOnReturn;
    private String notes;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Transaction() {
    }

    public Transaction(int id, int bookingId, int managerId, String managerName,
                       LocalDateTime issuedAt, LocalDateTime returnedAt,
                       String conditionOnReturn, String notes) {
        this.id = id;
        this.bookingId = bookingId;
        this.managerId = managerId;
        this.managerName = managerName;
        this.issuedAt = issuedAt;
        this.returnedAt = returnedAt;
        this.conditionOnReturn = conditionOnReturn;
        this.notes = notes;
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

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }

    public void setReturnedAt(LocalDateTime returnedAt) {
        this.returnedAt = returnedAt;
    }

    public String getConditionOnReturn() {
        return conditionOnReturn;
    }

    public void setConditionOnReturn(String conditionOnReturn) {
        this.conditionOnReturn = conditionOnReturn;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Transaction{id=" + id + ", bookingId=" + bookingId
                + ", managerId=" + managerId + ", managerName='" + managerName + "'"
                + ", issuedAt=" + issuedAt + ", returnedAt=" + returnedAt
                + ", conditionOnReturn='" + conditionOnReturn + "'"
                + ", notes='" + notes + "'}";
    }
}
