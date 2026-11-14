package com.sharespace.model;

import java.time.LocalDateTime;

/**
 * Represents an Owner user in the ShareSpace system.
 */
public class Owner extends User {

    private String department;
    private String contactNumber;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Owner() {
        super();
        setRole("OWNER");
    }

    public Owner(int id, String name, String email, String password,
                 boolean active, LocalDateTime createdAt,
                 String department, String contactNumber) {
        super(id, name, email, password, "OWNER", active, createdAt);
        this.department = department;
        this.contactNumber = contactNumber;
    }

    // -------------------------------------------------------------------------
    // Abstract method implementations
    // -------------------------------------------------------------------------

    @Override
    public void showDashboard() {
        System.out.println("=== OWNER DASHBOARD ===");
        System.out.println("Welcome, " + getName() + "!");
        System.out.println("Department     : " + department);
        System.out.println("Contact Number : " + contactNumber);
        System.out.println("Status         : " + (isActive() ? "Active" : "Inactive"));
    }

    @Override
    public String getRoleDescription() {
        return "Owner - Resource Provider";
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmployeeId() {
        return null;
    }

    public void setEmployeeId(String employeeId) {
        // Not used for owners
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Owner{id=" + getId() + ", name='" + getName() + "', email='" + getEmail()
                + "', department='" + department + "', contactNumber='" + contactNumber
                + "', active=" + isActive() + "}";
    }
}
