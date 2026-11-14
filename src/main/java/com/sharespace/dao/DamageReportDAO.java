package com.sharespace.dao;

import com.sharespace.database.DBConnection;
import com.sharespace.model.DamageReport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for DamageReport entities.
 * Provides insert and query operations for the damage_reports table.
 */
public class DamageReportDAO {

    // -------------------------------------------------------------------------
    // INSERT
    // -------------------------------------------------------------------------

    /**
     * Persists a new damage report and returns its generated primary key.
     *
     * @param report the DamageReport to persist
     * @return the generated report id, or -1 on failure
     * @throws SQLException on any database error
     */
    public int createReport(DamageReport report) throws SQLException {
        String sql = "INSERT INTO damage_reports "
                + "(transaction_id, resource_id, resource_name, damage_description, "
                + " severity, reported_by_manager_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, report.getTransactionId());
            ps.setInt(2, report.getResourceId());
            ps.setString(3, report.getResourceName());
            ps.setString(4, report.getDamageDescription());
            ps.setString(5, report.getSeverity());
            ps.setInt(6, report.getReportedByManagerId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        int generatedId = keys.getInt(1);
                        report.setId(generatedId);
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
     * Returns all damage reports in the system ordered by report time descending.
     *
     * @return list of all DamageReport objects
     * @throws SQLException on any database error
     */
    public List<DamageReport> findAll() throws SQLException {
        String sql = "SELECT * FROM damage_reports ORDER BY reported_at DESC";
        List<DamageReport> reports = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                reports.add(mapResultSetToDamageReport(rs));
            }
        }
        return reports;
    }

    /**
     * Returns all damage reports for a specific resource.
     *
     * @param resourceId the resource id
     * @return list of DamageReport objects for that resource, ordered by report time desc
     * @throws SQLException on any database error
     */
    public List<DamageReport> findByResourceId(int resourceId) throws SQLException {
        String sql = "SELECT * FROM damage_reports WHERE resource_id = ? ORDER BY reported_at DESC";
        List<DamageReport> reports = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, resourceId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToDamageReport(rs));
                }
            }
        }
        return reports;
    }

    // -------------------------------------------------------------------------
    // MAPPING
    // -------------------------------------------------------------------------

    /**
     * Maps the current row of a ResultSet to a DamageReport object.
     *
     * @param rs the ResultSet positioned at the row to map
     * @return a fully-populated DamageReport instance
     * @throws SQLException on any column access error
     */
    private DamageReport mapResultSetToDamageReport(ResultSet rs) throws SQLException {
        DamageReport report = new DamageReport();

        report.setId(rs.getInt("id"));
        report.setTransactionId(rs.getInt("transaction_id"));
        report.setResourceId(rs.getInt("resource_id"));
        report.setResourceName(rs.getString("resource_name"));
        report.setDamageDescription(rs.getString("damage_description"));
        report.setSeverity(rs.getString("severity"));
        report.setReportedByManagerId(rs.getInt("reported_by_manager_id"));

        Timestamp ts = rs.getTimestamp("reported_at");
        if (ts != null) {
            report.setReportedAt(ts.toLocalDateTime());
        }

        return report;
    }
}
