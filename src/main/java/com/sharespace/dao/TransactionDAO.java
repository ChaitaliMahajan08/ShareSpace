package com.sharespace.dao;

import com.sharespace.database.DBConnection;
import com.sharespace.model.Transaction;

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
 * Data Access Object for Transaction entities.
 * Tracks the physical issuance and return of resources.
 */
public class TransactionDAO {

    // -------------------------------------------------------------------------
    // INSERT
    // -------------------------------------------------------------------------

    /**
     * Records a new issuance transaction and returns its generated primary key.
     *
     * @param t the Transaction to persist
     * @return the generated transaction id, or -1 on failure
     * @throws SQLException on any database error
     */
    public int createTransaction(Transaction t) throws SQLException {
        String sql = "INSERT INTO transactions (booking_id, manager_id, manager_name) "
                + "VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, t.getBookingId());
            ps.setInt(2, t.getManagerId());
            ps.setString(3, t.getManagerName());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        int generatedId = keys.getInt(1);
                        t.setId(generatedId);
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
     * Finds a transaction by its primary key.
     *
     * @param id the transaction id
     * @return Optional containing the Transaction if found, empty otherwise
     * @throws SQLException on any database error
     */
    public Optional<Transaction> findById(int id) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTransaction(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Finds the transaction associated with a specific booking.
     *
     * @param bookingId the booking id
     * @return Optional containing the Transaction if found, empty otherwise
     * @throws SQLException on any database error
     */
    public Optional<Transaction> findByBookingId(int bookingId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE booking_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTransaction(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Returns all transactions in the system ordered by issuance time descending.
     *
     * @return list of all Transaction objects
     * @throws SQLException on any database error
     */
    public List<Transaction> findAll() throws SQLException {
        String sql = "SELECT * FROM transactions ORDER BY issued_at DESC";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }
        return transactions;
    }

    /**
     * Returns all transactions where the resource has not yet been returned
     * (i.e. returned_at IS NULL).
     *
     * @return list of currently-issued Transaction objects
     * @throws SQLException on any database error
     */
    public List<Transaction> findCurrentlyIssued() throws SQLException {
        String sql = "SELECT * FROM transactions WHERE returned_at IS NULL ORDER BY issued_at ASC";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }
        return transactions;
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    /**
     * Records the return of a resource for a given transaction.
     * Sets returned_at to the current timestamp, and stores the condition and notes.
     *
     * @param transactionId the transaction id
     * @param condition     condition of the resource on return (e.g. GOOD, DAMAGED)
     * @param notes         optional manager notes (may be null)
     * @return true if at least one row was updated
     * @throws SQLException on any database error
     */
    public boolean recordReturn(int transactionId, String condition, String notes) throws SQLException {
        String sql = "UPDATE transactions SET returned_at = NOW(), "
                + "condition_on_return = ?, notes = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, condition);
            ps.setString(2, notes);
            ps.setInt(3, transactionId);

            return ps.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------------------------
    // MAPPING
    // -------------------------------------------------------------------------

    /**
     * Maps the current row of a ResultSet to a Transaction object.
     *
     * @param rs the ResultSet positioned at the row to map
     * @return a fully-populated Transaction instance
     * @throws SQLException on any column access error
     */
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();

        t.setId(rs.getInt("id"));
        t.setBookingId(rs.getInt("booking_id"));
        t.setManagerId(rs.getInt("manager_id"));
        t.setManagerName(rs.getString("manager_name"));

        Timestamp issuedAt = rs.getTimestamp("issued_at");
        if (issuedAt != null) {
            t.setIssuedAt(issuedAt.toLocalDateTime());
        }

        Timestamp returnedAt = rs.getTimestamp("returned_at");
        if (returnedAt != null) {
            t.setReturnedAt(returnedAt.toLocalDateTime());
        }

        t.setConditionOnReturn(rs.getString("condition_on_return"));
        t.setNotes(rs.getString("notes"));

        return t;
    }
}
