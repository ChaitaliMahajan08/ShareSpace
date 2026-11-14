package com.sharespace.service;

import com.sharespace.dao.BookingDAO;
import com.sharespace.dao.DamageReportDAO;
import com.sharespace.dao.ResourceDAO;
import com.sharespace.dao.TransactionDAO;
import com.sharespace.dao.UserDAO;
import com.sharespace.model.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Provides summary and reporting functionality for the Admin.
 */
public class ReportService {
    private final UserDAO userDAO;
    private final ResourceDAO resourceDAO;
    private final BookingDAO bookingDAO;
    private final TransactionDAO transactionDAO;
    private final DamageReportDAO damageReportDAO;

    public ReportService() {
        this.userDAO = new UserDAO();
        this.resourceDAO = new ResourceDAO();
        this.bookingDAO = new BookingDAO();
        this.transactionDAO = new TransactionDAO();
        this.damageReportDAO = new DamageReportDAO();
    }

    public List<User> getAllUsers() throws SQLException { return userDAO.findAll(); }
    public List<User> getUsersByRole(String role) throws SQLException { return userDAO.findByRole(role); }
    public List<Resource> getAllResources() throws SQLException { return resourceDAO.findAll(); }
    public List<Booking> getAllBookings() throws SQLException { return bookingDAO.findAll(); }
    public List<Transaction> getAllTransactions() throws SQLException { return transactionDAO.findAll(); }
    public List<Transaction> getCurrentlyIssued() throws SQLException { return transactionDAO.findCurrentlyIssued(); }
    public List<DamageReport> getAllDamageReports() throws SQLException { return damageReportDAO.findAll(); }

    public String generateSystemSummary() throws SQLException {
        List<User> users = getAllUsers();
        List<Resource> resources = getAllResources();
        List<Booking> bookings = getAllBookings();
        List<Transaction> transactions = getAllTransactions();
        List<DamageReport> damageReports = getAllDamageReports();

        long students = users.stream().filter(u -> "STUDENT".equals(u.getRole())).count();
        long owners   = users.stream().filter(u -> "OWNER".equals(u.getRole())).count();
        long managers = users.stream().filter(u -> "MANAGER".equals(u.getRole())).count();
        long admins   = users.stream().filter(u -> "ADMIN".equals(u.getRole())).count();

        long pending  = bookings.stream().filter(b -> "PENDING".equals(b.getStatus())).count();
        long approved = bookings.stream().filter(b -> "APPROVED".equals(b.getStatus())).count();
        long issued   = bookings.stream().filter(b -> "ISSUED".equals(b.getStatus())).count();
        long returned = bookings.stream().filter(b -> "RETURNED".equals(b.getStatus())).count();

        StringBuilder sb = new StringBuilder();
        sb.append("\n========================================\n");
        sb.append("         SHARESPACE SYSTEM SUMMARY\n");
        sb.append("========================================\n");
        sb.append(String.format("  Total Users      : %d%n", users.size()));
        sb.append(String.format("    Students       : %d%n", students));
        sb.append(String.format("    Owners         : %d%n", owners));
        sb.append(String.format("    Managers       : %d%n", managers));
        sb.append(String.format("    Admins         : %d%n", admins));
        sb.append("----------------------------------------\n");
        sb.append(String.format("  Total Resources  : %d%n", resources.size()));
        sb.append(String.format("  Total Bookings   : %d%n", bookings.size()));
        sb.append(String.format("    Pending        : %d%n", pending));
        sb.append(String.format("    Approved       : %d%n", approved));
        sb.append(String.format("    Issued         : %d%n", issued));
        sb.append(String.format("    Returned       : %d%n", returned));
        sb.append("----------------------------------------\n");
        sb.append(String.format("  Total Transactions : %d%n", transactions.size()));
        sb.append(String.format("  Damage Reports   : %d%n", damageReports.size()));
        sb.append("========================================\n");
        return sb.toString();
    }

    public boolean addUser(User user) throws SQLException { return userDAO.registerUser(user); }
    public boolean updateUser(User user) throws SQLException { return userDAO.updateUser(user); }
    public boolean deactivateUser(int userId) throws SQLException { return userDAO.deactivateUser(userId); }
    public boolean activateUser(int userId) throws SQLException { return userDAO.activateUser(userId); }

    public List<DamageReport> getDamageReportsByResource(int resourceId) throws SQLException {
        return damageReportDAO.findByResourceId(resourceId);
    }

    public int createDamageReport(DamageReport report) throws SQLException {
        return damageReportDAO.createReport(report);
    }
}
