package com.sharespace.model;

import java.time.LocalDateTime;

/**
 * Abstract base class for all users in the ShareSpace system.
 */
public abstract class User {

    private int id;
    private String name;
    private String email;
    private String password;
    private String role;
    private boolean active;
    private LocalDateTime createdAt;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public User() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public User(int id, String name, String email, String password, String role,
                boolean active, LocalDateTime createdAt) {
        this.id = id;
        setName(name);
        setEmail(email);
        this.password = password;
        this.role = role;
        this.active = active;
        this.createdAt = createdAt;
    }

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    public abstract void showDashboard();

    public abstract String getRoleDescription();

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    /**
     * Sets the user name. Must not be null or blank after trimming.
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name must not be null or empty.");
        }
        this.name = normalizeName(name);
    }

    public String getEmail() {
        return email;
    }

    /**
     * Sets the user email. Must be a valid email format.
     */
    public void setEmail(String email) {
        if (!validateEmail(email)) {
            throw new IllegalArgumentException("Invalid email address: " + email);
        }
        this.email = normalizeEmail(email);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // -------------------------------------------------------------------------
    // Static utility methods
    // -------------------------------------------------------------------------

    /**
     * Trims and converts an email to lowercase.
     */
    public static String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase();
    }

    /**
     * Validates an email: not null, contains '@', and has at least one dot after '@'.
     */
    public static boolean validateEmail(String email) {
        if (email == null) return false;
        String trimmed = email.trim();
        int atIndex = trimmed.indexOf('@');
        if (atIndex < 1) return false;
        String afterAt = trimmed.substring(atIndex + 1);
        return afterAt.contains(".");
    }

    /**
     * Trims the name and capitalizes the first letter of each word.
     */
    public static String normalizeName(String name) {
        if (name == null) return null;
        String[] words = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (words[i].isEmpty()) continue;
            sb.append(Character.toUpperCase(words[i].charAt(0)));
            if (words[i].length() > 1) {
                sb.append(words[i].substring(1).toLowerCase());
            }
            if (i < words.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Instance methods
    // -------------------------------------------------------------------------

    /**
     * Builds a multi-line profile report using StringBuilder.
     */
    public String generateProfileReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("           USER PROFILE REPORT          \n");
        sb.append("========================================\n");
        sb.append("ID          : ").append(id).append("\n");
        sb.append("Name        : ").append(name).append("\n");
        sb.append("Email       : ").append(email).append("\n");
        sb.append("Role        : ").append(role).append("\n");
        sb.append("Description : ").append(getRoleDescription()).append("\n");
        sb.append("Active      : ").append(active ? "Yes" : "No").append("\n");
        sb.append("Created At  : ").append(createdAt).append("\n");
        sb.append("========================================\n");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email
                + "', role='" + role + "'}";
    }
}
