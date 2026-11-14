package com.sharespace.model;

import java.time.LocalDateTime;

/**
 * Represents a Student user in the ShareSpace system.
 */
public class Student extends User {

    private String department;
    private int year; // 1–4

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Student() {
        super();
        setRole("STUDENT");
    }

    public Student(int id, String name, String email, String password,
                   boolean active, LocalDateTime createdAt,
                   String department, int year) {
        super(id, name, email, password, "STUDENT", active, createdAt);
        this.department = department;
        setYear(year);
    }

    // -------------------------------------------------------------------------
    // Abstract method implementations
    // -------------------------------------------------------------------------

    @Override
    public void showDashboard() {
        System.out.println("=== STUDENT DASHBOARD ===");
        System.out.println("Welcome, " + getName() + "!");
        System.out.println("Department : " + department);
        System.out.println("Year       : " + year);
        System.out.println("Status     : " + (isActive() ? "Active" : "Inactive"));
    }

    @Override
    public String getRoleDescription() {
        return "Student - Resource Borrower";
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

    public int getYear() {
        return year;
    }

    public int getYearOfStudy() {
        return year;
    }

    public void setYearOfStudy(int yearOfStudy) {
        setYear(yearOfStudy);
    }

    public String getContactNumber() {
        return null;
    }

    public void setContactNumber(String contactNumber) {
        // Optional for students
    }

    /**
     * Sets the academic year. Must be between 1 and 4 inclusive.
     */
    public void setYear(int year) {
        if (year < 1 || year > 4) {
            throw new IllegalArgumentException("Year must be between 1 and 4, got: " + year);
        }
        this.year = year;
    }

    // -------------------------------------------------------------------------
    // Business methods
    // -------------------------------------------------------------------------

    /**
     * Returns true if the student is active and in a valid year.
     */
    public boolean isEligibleToBorrow() {
        return isActive() && year >= 1;
    }

    /**
     * Parses a year string to an Integer. Returns null if parsing fails.
     */
    public static Integer parseYear(String s) {
        if (s == null) return null;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Student{id=" + getId() + ", name='" + getName() + "', email='" + getEmail()
                + "', department='" + department + "', year=" + year
                + ", active=" + isActive() + "}";
    }
}
