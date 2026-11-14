package com.sharespace.dao;

import com.sharespace.database.DBConnection;
import com.sharespace.model.Booking;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Booking entities.
 * Provides CRUD and query operations for the bookings table.
 */
public class BookingDAO {

    // -------------------------------------------------------------------------
    // INSERT
    // -------------------------------------------------------------------------

    /**
     * Creates a new booking and returns its generated primary key.
     *
     * @param booking the Booking to persist (status defaults to PENDING)
     * @return the generated booking id, or -1 on failure
     * @throws SQLException on any database error
     */
    public int createBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings "
                + "(student_id, student_name, resource_id, resource_name, "
                + " quantity_requested, start_date, end_date, status, remarks) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, booking.getStudentId());
            ps.setString(2, booking.getStudentName());
            ps.setInt(3, booking.getResourceId());
            ps.setString(4, booking.getResourceName());
            ps.setInt(5, booking.getQuantityRequested());
            ps.setDate(6, Date.valueOf(booking.getStartDate()));
            ps.setDate(7, Date.valueOf(booking.getEndDate()));
            ps.setString(8, booking.getStatus() != null ? booking.getStatus() : "PENDING");
            ps.setString(9, booking.getRemarks());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        int generatedId = keys.getInt(1);
                        booking.setId(generatedId);
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
     * Finds a booking by its primary key.
     *
     * @param id the booking id
     * @return Optional containing the Booking if found, empty otherwise
     * @throws SQLException on any database error
     */
    public Optional<Booking> findById(int id) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBooking(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Returns all bookings made by a specific student.
     *
     * @param studentId the student's user id
     * @return list of matching Booking objects ordered by creation date desc
     * @throws SQLException on any database error
     */
    public List<Booking> findByStudentId(int studentId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE student_id = ? ORDER BY created_at DESC";
        List<Booking> bookings = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        }
        return bookings;
    }

    /**
     * Returns all bookings for a specific resource.
     *
     * @param resourceId the resource id
     * @return list of matching Booking objects ordered by start date
     * @throws SQLException on any database error
     */
    public List<Booking> findByResourceId(int resourceId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE resource_id = ? ORDER BY start_date ASC";
        List<Booking> bookings = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, resourceId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        }
        return bookings;
    }

    /**
     * Returns all bookings that have a given status.
     *
     * @param status one of PENDING, APPROVED, REJECTED, ISSUED, RETURNED, CANCELLED
     * @return list of matching Booking objects ordered by creation date desc
     * @throws SQLException on any database error
     */
    public List<Booking> findByStatus(String status) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE status = ? ORDER BY created_at DESC";
        List<Booking> bookings = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.toUpperCase());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        }
        return bookings;
    }

    /**
     * Returns all PENDING bookings for resources owned by a given owner.
     * Performs a JOIN between bookings and resources tables.
     *
     * @param ownerId the owner's user id
     * @return list of pending Booking objects for that owner's resources
     * @throws SQLException on any database error
     */
    public List<Booking> findPendingByOwnerId(int ownerId) throws SQLException {
        String sql = "SELECT b.* FROM bookings b "
                + "JOIN resources r ON b.resource_id = r.id "
                + "WHERE r.owner_id = ? AND b.status = 'PENDING' "
                + "ORDER BY b.created_at ASC";
        List<Booking> bookings = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ownerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        }
        return bookings;
    }

    /**
     * Returns all bookings for resources owned by a given owner (all statuses).
     *
     * @param ownerId the owner's user id
     * @return list of Booking objects for that owner's resources
     * @throws SQLException on any database error
     */
    public List<Booking> findByOwnerId(int ownerId) throws SQLException {
        String sql = "SELECT b.* FROM bookings b "
                + "JOIN resources r ON b.resource_id = r.id "
                + "WHERE r.owner_id = ? "
                + "ORDER BY b.created_at DESC";
        List<Booking> bookings = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ownerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        }
        return bookings;
    }

    /**
     * Returns all bookings with status APPROVED.
     *
     * @return list of approved Booking objects
     * @throws SQLException on any database error
     */
    public List<Booking> findApprovedBookings() throws SQLException {
        String sql = "SELECT * FROM bookings WHERE status = 'APPROVED' ORDER BY start_date ASC";
        List<Booking> bookings = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        }
        return bookings;
    }

    /**
     * Returns every booking in the system.
     *
     * @return list of all Booking objects ordered by creation date desc
     * @throws SQLException on any database error
     */
    public List<Booking> findAll() throws SQLException {
        String sql = "SELECT * FROM bookings ORDER BY created_at DESC";
        List<Booking> bookings = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        }
        return bookings;
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    /**
     * Updates the status and optional remarks of a booking.
     *
     * @param bookingId the booking id
     * @param newStatus the new status value
     * @param remarks   optional remarks/notes (may be null)
     * @return true if at least one row was updated
     * @throws SQLException on any database error
     */
    public boolean updateStatus(int bookingId, String newStatus, String remarks) throws SQLException {
        String sql = "UPDATE bookings SET status = ?, remarks = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus.toUpperCase());
            ps.setString(2, remarks);
            ps.setInt(3, bookingId);

            return ps.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------------------------
    // OVERLAP CHECK
    // -------------------------------------------------------------------------

    /**
     * Checks whether a resource has an overlapping active booking in the given date range.
     * Active bookings are those with status PENDING or APPROVED.
     * A booking with id equal to {@code excludeBookingId} is excluded from the check
     * (useful when editing an existing booking).
     *
     * @param resourceId        the resource to check
     * @param start             the desired start date (inclusive)
     * @param end               the desired end date (inclusive)
     * @param excludeBookingId  id of a booking to exclude (pass 0 to exclude none)
     * @return true if at least one overlapping booking exists
     * @throws SQLException on any database error
     */
    public boolean hasOverlappingBooking(int resourceId, LocalDate start, LocalDate end,
                                         int excludeBookingId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings "
                + "WHERE resource_id = ? "
                + "AND id <> ? "
                + "AND status IN ('PENDING', 'APPROVED') "
                + "AND start_date <= ? "
                + "AND end_date >= ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, resourceId);
            ps.setInt(2, excludeBookingId);
            ps.setDate(3, Date.valueOf(end));
            ps.setDate(4, Date.valueOf(start));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // MAPPING
    // -------------------------------------------------------------------------

    /**
     * Maps the current row of a ResultSet to a Booking object.
     *
     * @param rs the ResultSet positioned at the row to map
     * @return a fully-populated Booking instance
     * @throws SQLException on any column access error
     */
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();

        booking.setId(rs.getInt("id"));
        booking.setStudentId(rs.getInt("student_id"));
        booking.setStudentName(rs.getString("student_name"));
        booking.setResourceId(rs.getInt("resource_id"));
        booking.setResourceName(rs.getString("resource_name"));
        booking.setQuantityRequested(rs.getInt("quantity_requested"));

        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            booking.setStartDate(startDate.toLocalDate());
        }

        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            booking.setEndDate(endDate.toLocalDate());
        }

        booking.setStatus(rs.getString("status"));
        booking.setRemarks(rs.getString("remarks"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            booking.setCreatedAt(ts.toLocalDateTime());
        }

        return booking;
    }
}
