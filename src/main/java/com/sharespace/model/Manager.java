package com.sharespace.model;

import java.time.LocalDateTime;

/**
 * Represents a Manager user in the ShareSpace system.
 */
public class Manager extends User {

    private String employeeId;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Manager() {
        super();
        setRole("MANAGER");
    }

    public Manager(int id, String name, String email, String password,
                   boolean active, LocalDateTime createdAt,
                   String employeeId) {
        super(id, name, email, password, "MANAGER", active, createdAt);
        this.employeeId = employeeId;
    }

    // -------------------------------------------------------------------------
    // Abstract method implementations
    // -------------------------------------------------------------------------

    @Override
    public void showDashboard() {
        System.out.println("=== MANAGER DASHBOARD ===");
        System.out.println("Welcome, " + getName() + "!");
        System.out.println("Employee ID : " + employeeId);
        System.out.println("Status      : " + (isActive() ? "Active" : "Inactive"));
    }

    @Override
    public String getRoleDescription() {
        return "Manager - Resource Handler";
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDepartment() {
        return null;
    }

    public void setDepartment(String department) {
        // Optional for manager
    }

    public String getContactNumber() {
        return null;
    }

    public void setContactNumber(String contactNumber) {
        // Optional for manager
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Manager{id=" + getId() + ", name='" + getName() + "', email='" + getEmail()
                + "', employeeId='" + employeeId + "', active=" + isActive() + "}";
    }
}
