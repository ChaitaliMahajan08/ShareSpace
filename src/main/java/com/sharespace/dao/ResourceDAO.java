package com.sharespace.dao;

import com.sharespace.database.DBConnection;
import com.sharespace.model.Resource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Resource entities.
 * Provides CRUD and query operations for the resources table.
 */
public class ResourceDAO {

    // -------------------------------------------------------------------------
    // INSERT
    // -------------------------------------------------------------------------

    /**
     * Inserts a new resource and returns its generated primary key.
     *
     * @param resource the Resource to persist
     * @return the generated resource id, or -1 on failure
     * @throws SQLException on any database error
     */
    public int addResource(Resource resource) throws SQLException {
        String sql = "INSERT INTO resources "
                + "(name, category, description, total_quantity, available_quantity, "
                + " location, owner_id, owner_name, active) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, resource.getName());
            ps.setString(2, resource.getCategory());
            ps.setString(3, resource.getDescription());
            ps.setInt(4, resource.getTotalQuantity());
            ps.setInt(5, resource.getAvailableQuantity());
            ps.setString(6, resource.getLocation());
            ps.setInt(7, resource.getOwnerId());
            ps.setString(8, resource.getOwnerName());
            ps.setBoolean(9, resource.isActive());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        int generatedId = keys.getInt(1);
                        resource.setId(generatedId);
                        return generatedId;
                    }
                }
            }
        }
        return -1;
    }

    // -------------------------------------------------------------------------
    // SELECT
    // -------------------------------------------------------------------------

    /**
     * Finds a resource by its primary key.
     *
     * @param id the resource id
     * @return Optional containing the Resource if found, empty otherwise
     * @throws SQLException on any database error
     */
    public Optional<Resource> findById(int id) throws SQLException {
        String sql = "SELECT * FROM resources WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToResource(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Returns all resources in the system.
     *
     * @return list of all Resource objects
     * @throws SQLException on any database error
     */
    public List<Resource> findAll() throws SQLException {
        String sql = "SELECT * FROM resources ORDER BY id ASC";
        List<Resource> resources = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resources.add(mapResultSetToResource(rs));
            }
        }
        return resources;
    }

    /**
     * Returns all resources belonging to a specific owner.
     *
     * @param ownerId the owner's user id
     * @return list of matching Resource objects
     * @throws SQLException on any database error
     */
    public List<Resource> findByOwnerId(int ownerId) throws SQLException {
        String sql = "SELECT * FROM resources WHERE owner_id = ? ORDER BY id ASC";
        List<Resource> resources = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ownerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resources.add(mapResultSetToResource(rs));
                }
            }
        }
        return resources;
    }

    /**
     * Returns all resources that currently have stock available and are active.
     *
     * @return list of available Resource objects
     * @throws SQLException on any database error
     */
    public List<Resource> findAvailable() throws SQLException {
        String sql = "SELECT * FROM resources WHERE available_quantity > 0 AND active = TRUE ORDER BY id ASC";
        List<Resource> resources = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resources.add(mapResultSetToResource(rs));
            }
        }
        return resources;
    }

    /**
     * Returns all resources belonging to a specific category.
     *
     * @param category the category name (case-insensitive match attempted server-side)
     * @return list of matching Resource objects
     * @throws SQLException on any database error
     */
    public List<Resource> findByCategory(String category) throws SQLException {
        String sql = "SELECT * FROM resources WHERE category = ? ORDER BY id ASC";
        List<Resource> resources = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resources.add(mapResultSetToResource(rs));
                }
            }
        }
        return resources;
    }

    /**
     * Searches resources whose name contains the given keyword (case-insensitive).
     *
     * @param keyword the search term
     * @return list of matching Resource objects
     * @throws SQLException on any database error
     */
    public List<Resource> searchByName(String keyword) throws SQLException {
        String sql = "SELECT * FROM resources WHERE name LIKE ? ORDER BY id ASC";
        List<Resource> resources = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resources.add(mapResultSetToResource(rs));
                }
            }
        }
        return resources;
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    /**
     * Updates the editable fields of an existing resource.
     *
     * @param resource the Resource with updated fields; id must match an existing row
     * @return true if at least one row was updated
     * @throws SQLException on any database error
     */
    public boolean updateResource(Resource resource) throws SQLException {
        String sql = "UPDATE resources SET name = ?, category = ?, description = ?, "
                + "total_quantity = ?, available_quantity = ?, location = ?, "
                + "owner_id = ?, owner_name = ?, active = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, resource.getName());
            ps.setString(2, resource.getCategory());
            ps.setString(3, resource.getDescription());
            ps.setInt(4, resource.getTotalQuantity());
            ps.setInt(5, resource.getAvailableQuantity());
            ps.setString(6, resource.getLocation());
            ps.setInt(7, resource.getOwnerId());
            ps.setString(8, resource.getOwnerName());
            ps.setBoolean(9, resource.isActive());
            ps.setInt(10, resource.getId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Updates only the available_quantity column for a resource.
     *
     * @param resourceId the resource id
     * @param newQty     the new available quantity value
     * @return true if at least one row was updated
     * @throws SQLException on any database error
     */
    public boolean updateAvailableQuantity(int resourceId, int newQty) throws SQLException {
        String sql = "UPDATE resources SET available_quantity = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newQty);
            ps.setInt(2, resourceId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deactivates a resource so it no longer appears in availability searches.
     *
     * @param resourceId the resource id
     * @return true if at least one row was updated
     * @throws SQLException on any database error
     */
    public boolean deactivateResource(int resourceId) throws SQLException {
        String sql = "UPDATE resources SET active = FALSE WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, resourceId);
            return ps.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------------------------
    // MAPPING
    // -------------------------------------------------------------------------

    /**
     * Maps the current row of a ResultSet to a Resource object.
     *
     * @param rs the ResultSet positioned at the row to map
     * @return a fully-populated Resource instance
     * @throws SQLException on any column access error
     */
    private Resource mapResultSetToResource(ResultSet rs) throws SQLException {
        Resource resource = new Resource();

        resource.setId(rs.getInt("id"));
        resource.setName(rs.getString("name"));
        resource.setCategory(rs.getString("category"));
        resource.setDescription(rs.getString("description"));
        resource.setTotalQuantity(rs.getInt("total_quantity"));
        resource.setAvailableQuantity(rs.getInt("available_quantity"));
        resource.setLocation(rs.getString("location"));
        resource.setOwnerId(rs.getInt("owner_id"));
        resource.setOwnerName(rs.getString("owner_name"));
        resource.setActive(rs.getBoolean("active"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            resource.setCreatedAt(ts.toLocalDateTime());
        }

        return resource;
    }
}
