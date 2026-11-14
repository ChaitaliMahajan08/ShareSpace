package com.sharespace.service;

import com.sharespace.dao.BookingDAO;
import com.sharespace.dao.DamageReportDAO;
import com.sharespace.dao.ResourceDAO;
import com.sharespace.dao.TransactionDAO;
import com.sharespace.database.DBConnection;
import com.sharespace.exceptions.BookingException;
import com.sharespace.model.Booking;
import com.sharespace.model.DamageReport;
import com.sharespace.model.Resource;
import com.sharespace.model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Core booking workflow service.
 * Handles booking creation, approval, rejection, cancellation, issue, and return.
 * Uses database transactions for multi-step operations (Milestone 5 requirement).
 */
public class BookingService {

    private final BookingDAO bookingDAO;
    private final ResourceDAO resourceDAO;
    private final TransactionDAO transactionDAO;
    private final DamageReportDAO damageReportDAO;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.resourceDAO = new ResourceDAO();
        this.transactionDAO = new TransactionDAO();
        this.damageReportDAO = new DamageReportDAO();
    }

    /**
     * Creates a new PENDING booking request for a student.
     * Validates dates, resource availability, and duplicate overlapping bookings.
     */
    public Booking createBookingRequest(int studentId, String studentName,
                                        int resourceId, LocalDate startDate,
                                        LocalDate endDate, int quantityRequested)
            throws BookingException, SQLException {

        if (startDate == null || endDate == null) {
            throw new BookingException("Start date and end date are required.");
        }
        if (!endDate.isAfter(startDate)) {
            throw new BookingException("End date must be after start date.");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new BookingException("Start date cannot be in the past.");
        }
        if (quantityRequested < 1) {
            throw new BookingException("Quantity requested must be at least 1.");
        }

        Optional<Resource> opt = resourceDAO.findById(resourceId);
        if (opt.isEmpty() || !opt.get().isActive()) {
            throw new BookingException("Resource not found or is inactive.");
        }
        Resource resource = opt.get();

        if (resource.getAvailableQuantity() < quantityRequested) {
            throw new BookingException("Insufficient availability. Available: "
                    + resource.getAvailableQuantity() + ", Requested: " + quantityRequested);
        }

        boolean overlap = bookingDAO.hasOverlappingBooking(resourceId, startDate, endDate, 0);
        if (overlap) {
            throw new BookingException("An active booking already exists for this resource during the requested period.");
        }

        Booking booking = new Booking();
        booking.setStudentId(studentId);
        booking.setStudentName(studentName);
        booking.setResourceId(resourceId);
        booking.setResourceName(resource.getName());
        booking.setQuantityRequested(quantityRequested);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setStatus("PENDING");
        booking.setCreatedAt(LocalDateTime.now());

        int bookingId = bookingDAO.createBooking(booking);
        booking.setId(bookingId);
        return booking;
    }

    /**
     * Owner approves a booking request.
     * Uses a DB transaction: updates booking status AND reduces resource availability atomically.
     * Rolls back if any step fails.
     */
    public boolean approveBooking(int bookingId, int ownerId)
            throws BookingException, SQLException {

        Optional<Booking> optBooking = bookingDAO.findById(bookingId);
        if (optBooking.isEmpty()) {
            throw new BookingException("Booking not found with ID: " + bookingId);
        }
        Booking booking = optBooking.get();

        if (!"PENDING".equalsIgnoreCase(booking.getStatus())) {
            throw new BookingException("Only PENDING bookings can be approved. Current status: " + booking.getStatus());
        }

        Optional<Resource> optResource = resourceDAO.findById(booking.getResourceId());
        if (optResource.isEmpty()) {
            throw new BookingException("Associated resource not found.");
        }
        Resource resource = optResource.get();

        if (resource.getOwnerId() != ownerId) {
            throw new BookingException("You can only approve bookings for your own resources.");
        }
        if (resource.getAvailableQuantity() < booking.getQuantityRequested()) {
            throw new BookingException("Insufficient quantity available to approve this booking.");
        }

        // DATABASE TRANSACTION: approve booking + reduce available quantity atomically
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            // Step 1: Update booking status to APPROVED
            String updateBooking = "UPDATE bookings SET status = 'APPROVED', remarks = 'Approved by owner' WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateBooking)) {
                ps.setInt(1, bookingId);
                ps.executeUpdate();
            }

            // Step 2: Reduce available quantity
            int newQty = resource.getAvailableQuantity() - booking.getQuantityRequested();
            if (newQty < 0) newQty = 0;
            String updateResource = "UPDATE resources SET available_quantity = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateResource)) {
                ps.setInt(1, newQty);
                ps.setInt(2, booking.getResourceId());
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignore) {}
            throw new BookingException("Failed to approve booking. Transaction rolled back. " + e.getMessage(), e);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignore) {}
        }
    }

    /**
     * Owner rejects a pending booking request.
     */
    public boolean rejectBooking(int bookingId, int ownerId, String remarks)
            throws BookingException, SQLException {

        Optional<Booking> optBooking = bookingDAO.findById(bookingId);
        if (optBooking.isEmpty()) {
            throw new BookingException("Booking not found with ID: " + bookingId);
        }
        Booking booking = optBooking.get();

        if (!"PENDING".equalsIgnoreCase(booking.getStatus())) {
            throw new BookingException("Only PENDING bookings can be rejected. Current status: " + booking.getStatus());
        }

        Optional<Resource> optResource = resourceDAO.findById(booking.getResourceId());
        if (optResource.isEmpty() || optResource.get().getOwnerId() != ownerId) {
            throw new BookingException("You can only reject bookings for your own resources.");
        }

        return bookingDAO.updateStatus(bookingId, "REJECTED",
                remarks != null && !remarks.trim().isEmpty() ? remarks.trim() : "Rejected by owner");
    }

    /**
     * Student cancels a PENDING booking.
     */
    public boolean cancelBooking(int bookingId, int studentId)
            throws BookingException, SQLException {

        Optional<Booking> optBooking = bookingDAO.findById(bookingId);
        if (optBooking.isEmpty()) {
            throw new BookingException("Booking not found with ID: " + bookingId);
        }
        Booking booking = optBooking.get();

        if (booking.getStudentId() != studentId) {
            throw new BookingException("You can only cancel your own bookings.");
        }
        if (!booking.isCancellable()) {
            throw new BookingException("Only PENDING bookings can be cancelled. Current status: " + booking.getStatus());
        }

        return bookingDAO.updateStatus(bookingId, "CANCELLED", "Cancelled by student");
    }

    /**
     * Manager issues an approved resource to a student.
     * Updates booking status to ISSUED and creates a Transaction record.
     */
    public Transaction issueResource(int bookingId, int managerId, String managerName)
            throws BookingException, SQLException {

        Optional<Booking> optBooking = bookingDAO.findById(bookingId);
        if (optBooking.isEmpty()) {
            throw new BookingException("Booking not found with ID: " + bookingId);
        }
        Booking booking = optBooking.get();

        if (!"APPROVED".equalsIgnoreCase(booking.getStatus())) {
            throw new BookingException("Only APPROVED bookings can be issued. Current status: " + booking.getStatus());
        }

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setBookingId(bookingId);
        transaction.setManagerId(managerId);
        transaction.setManagerName(managerName);
        transaction.setIssuedAt(LocalDateTime.now());

        int transactionId = transactionDAO.createTransaction(transaction);
        transaction.setId(transactionId);

        // Update booking status to ISSUED
        bookingDAO.updateStatus(bookingId, "ISSUED", "Resource issued by manager: " + managerName);

        return transaction;
    }

    /**
     * Manager records resource return.
     * Uses a DB transaction to: record return in transaction table + update booking to RETURNED
     * + restore available quantity (unless DAMAGED).
     */
    public boolean returnResource(int transactionId, int managerId,
                                   String conditionOnReturn, String notes)
            throws BookingException, SQLException {

        Optional<Transaction> optTrans = transactionDAO.findById(transactionId);
        if (optTrans.isEmpty()) {
            throw new BookingException("Transaction not found with ID: " + transactionId);
        }
        Transaction transaction = optTrans.get();

        if (transaction.getReturnedAt() != null) {
            throw new BookingException("This resource has already been returned.");
        }

        Optional<Booking> optBooking = bookingDAO.findById(transaction.getBookingId());
        if (optBooking.isEmpty()) {
            throw new BookingException("Associated booking not found.");
        }
        Booking booking = optBooking.get();

        Optional<Resource> optResource = resourceDAO.findById(booking.getResourceId());
        if (optResource.isEmpty()) {
            throw new BookingException("Associated resource not found.");
        }
        Resource resource = optResource.get();

        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            // Step 1: Record return in transaction
            String updateTrans = "UPDATE transactions SET returned_at = NOW(), condition_on_return = ?, notes = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateTrans)) {
                ps.setString(1, conditionOnReturn);
                ps.setString(2, notes != null ? notes : "");
                ps.setInt(3, transactionId);
                ps.executeUpdate();
            }

            // Step 2: Update booking status to RETURNED
            String updateBooking = "UPDATE bookings SET status = 'RETURNED', remarks = 'Returned to manager' WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateBooking)) {
                ps.setInt(1, transaction.getBookingId());
                ps.executeUpdate();
            }

            // Step 3: Restore available quantity (skip if DAMAGED)
            boolean damaged = "DAMAGED".equalsIgnoreCase(conditionOnReturn);
            if (!damaged) {
                int restoredQty = Math.min(
                        resource.getAvailableQuantity() + booking.getQuantityRequested(),
                        resource.getTotalQuantity()
                );
                String updateResource = "UPDATE resources SET available_quantity = ? WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(updateResource)) {
                    ps.setInt(1, restoredQty);
                    ps.setInt(2, booking.getResourceId());
                    ps.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignore) {}
            throw new BookingException("Failed to record return. Transaction rolled back. " + e.getMessage(), e);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignore) {}
        }
    }

    // --- Query methods ---

    public List<Booking> getStudentBookings(int studentId) throws SQLException {
        return bookingDAO.findByStudentId(studentId);
    }

    public List<Booking> getPendingBookingsForOwner(int ownerId) throws SQLException {
        return bookingDAO.findPendingByOwnerId(ownerId);
    }

    public List<Booking> getBookingsByOwnerResources(int ownerId) throws SQLException {
        return bookingDAO.findByOwnerId(ownerId);
    }

    public List<Booking> getApprovedBookings() throws SQLException {
        return bookingDAO.findApprovedBookings();
    }

    public List<Booking> getAllBookings() throws SQLException {
        return bookingDAO.findAll();
    }

    public Optional<Booking> findBookingById(int id) throws SQLException {
        return bookingDAO.findById(id);
    }

    public List<Booking> getBookingsByResource(int resourceId) throws SQLException {
        return bookingDAO.findByResourceId(resourceId);
    }

    public List<Transaction> getAllTransactions() throws SQLException {
        return transactionDAO.findAll();
    }

    public List<Transaction> getCurrentlyIssuedTransactions() throws SQLException {
        return transactionDAO.findCurrentlyIssued();
    }

    public Optional<Transaction> findTransactionById(int id) throws SQLException {
        return transactionDAO.findById(id);
    }

    public int createDamageReport(DamageReport report) throws SQLException {
        return damageReportDAO.createReport(report);
    }

    public List<DamageReport> getAllDamageReports() throws SQLException {
        return damageReportDAO.findAll();
    }
}
