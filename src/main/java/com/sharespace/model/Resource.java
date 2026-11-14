package com.sharespace.model;

import java.time.LocalDateTime;

/**
 * Represents a shareable resource in the ShareSpace system.
 */
public class Resource implements Comparable<Resource> {

    private int id;
    private String name;
    private String category;
    private String description;
    private int totalQuantity;
    private int availableQuantity;
    private String location;
    private int ownerId;
    private String ownerName;
    private boolean active;
    private LocalDateTime createdAt;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Resource() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public Resource(int id, String name, String category, String description,
                    int totalQuantity, int availableQuantity, String location,
                    int ownerId, String ownerName, boolean active, LocalDateTime createdAt) {
        this.id = id;
        setName(name);
        this.category = category;
        this.description = description;
        setTotalQuantity(totalQuantity);
        setAvailableQuantity(availableQuantity);
        this.location = location;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.active = active;
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

    public String getName() {
        return name;
    }

    /**
     * Sets the resource name. Must not be null or blank.
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Resource name must not be null or empty.");
        }
        this.name = name.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    /**
     * Sets total quantity. Must be >= 0.
     */
    public void setTotalQuantity(int totalQuantity) {
        if (totalQuantity < 0) {
            throw new IllegalArgumentException("Total quantity must be >= 0.");
        }
        this.totalQuantity = totalQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    /**
     * Sets available quantity. Must be >= 0.
     */
    public void setAvailableQuantity(int availableQuantity) {
        if (availableQuantity < 0) {
            throw new IllegalArgumentException("Available quantity must be >= 0.");
        }
        this.availableQuantity = availableQuantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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
    // Business methods
    // -------------------------------------------------------------------------

    /**
     * Builds a detailed resource report using StringBuilder.
     */
    public String generateResourceReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("         RESOURCE REPORT                \n");
        sb.append("========================================\n");
        sb.append("ID                 : ").append(id).append("\n");
        sb.append("Name               : ").append(name).append("\n");
        sb.append("Category           : ").append(category).append("\n");
        sb.append("Description        : ").append(description).append("\n");
        sb.append("Total Quantity     : ").append(totalQuantity).append("\n");
        sb.append("Available Quantity : ").append(availableQuantity).append("\n");
        sb.append("Location           : ").append(location).append("\n");
        sb.append("Owner ID           : ").append(ownerId).append("\n");
        sb.append("Owner Name         : ").append(ownerName).append("\n");
        sb.append("Active             : ").append(active ? "Yes" : "No").append("\n");
        sb.append("Created At         : ").append(createdAt).append("\n");
        sb.append("========================================\n");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Comparable implementation (natural order: alphabetical by name)
    // -------------------------------------------------------------------------

    @Override
    public int compareTo(Resource other) {
        if (other == null) return 1;
        return this.name.compareToIgnoreCase(other.name);
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Resource{id=" + id + ", name='" + name + "', category='" + category
                + "', totalQuantity=" + totalQuantity + ", availableQuantity=" + availableQuantity
                + ", location='" + location + "', active=" + active + "}";
    }
}
