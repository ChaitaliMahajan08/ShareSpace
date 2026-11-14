package com.sharespace.model;

import java.time.LocalDateTime;

/**
 * Represents an Admin user in the ShareSpace system.
 */
public class Admin extends User {

    private String adminCode;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Admin() {
        super();
        setRole("ADMIN");
    }

    public Admin(int id, String name, String email, String password,
                 boolean active, LocalDateTime createdAt,
                 String adminCode) {
        super(id, name, email, password, "ADMIN", active, createdAt);
        this.adminCode = adminCode;
    }

    // -------------------------------------------------------------------------
    // Abstract method implementations
    // -------------------------------------------------------------------------

    @Override
    public void showDashboard() {
        System.out.println("=== ADMIN DASHBOARD ===");
        System.out.println("Welcome, " + getName() + "!");
        System.out.println("Admin Code : " + adminCode);
        System.out.println("Status     : " + (isActive() ? "Active" : "Inactive"));
    }

    @Override
    public String getRoleDescription() {
        return "Administrator - System Manager";
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------

    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode) {
        this.adminCode = adminCode;
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Admin{id=" + getId() + ", name='" + getName() + "', email='" + getEmail()
                + "', adminCode='" + adminCode + "', active=" + isActive() + "}";
    }
}
