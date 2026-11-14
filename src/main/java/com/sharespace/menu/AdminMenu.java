package com.sharespace.menu;

import com.sharespace.model.*;
import com.sharespace.service.BookingService;
import com.sharespace.service.ReportService;
import com.sharespace.service.ResourceService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Admin dashboard menu.
 * Full system oversight: users, resources, bookings, transactions, reports.
 */
public class AdminMenu {

    private final Scanner scanner;
    private final ReportService  reportService;
    private final ResourceService resourceService;
    private final BookingService  bookingService;

    public AdminMenu(Scanner scanner) {
        this.scanner          = scanner;
        this.reportService    = new ReportService();
        this.resourceService  = new ResourceService();
        this.bookingService   = new BookingService();
    }

    public void show(Admin admin) {
        boolean running = true;
        while (running) {
            printDashboard(admin.getName());
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> viewAllUsers();
                case "2" -> viewUsersByRole();
                case "3" -> manageUser();
                case "4" -> viewAllResources();
                case "5" -> viewAllBookings();
                case "6" -> viewAllTransactions();
                case "7" -> viewDamageReports();
                case "8" -> viewSystemSummary();
                case "9" -> { System.out.println("\n[Admin] Logged out successfully.\n"); running = false; }
                default  -> System.out.println("\n[!] Invalid choice.\n");
            }
        }
    }

    private void printDashboard(String name) {
        System.out.println("\n" + "=".repeat(44));
        System.out.println("  ADMIN DASHBOARD — " + name);
        System.out.println("=".repeat(44));
        System.out.println("  1. View All Users");
        System.out.println("  2. View Users by Role");
        System.out.println("  3. Add / Update / Deactivate User");
        System.out.println("  4. View All Resources");
        System.out.println("  5. View All Bookings");
        System.out.println("  6. View All Transactions");
        System.out.println("  7. View Damage Reports");
        System.out.println("  8. View System Summary");
        System.out.println("  9. Logout");
        System.out.println("=".repeat(44));
    }

    private void viewAllUsers() {
        System.out.println("\n--- ALL USERS ---");
        try {
            List<User> users = reportService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("No users found.");
            } else {
                printUserTable(users);
            }
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void viewUsersByRole() {
        System.out.println("\n--- VIEW USERS BY ROLE ---");
        System.out.println("Roles: STUDENT | OWNER | MANAGER | ADMIN");
        System.out.print("Enter role: ");
        String role = scanner.nextLine().trim().toUpperCase();
        try {
            List<User> users = reportService.getUsersByRole(role);
            if (users.isEmpty()) {
                System.out.println("No users found for role: " + role);
            } else {
                printUserTable(users);
            }
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void manageUser() {
        System.out.println("\n--- MANAGE USER ---");
        System.out.println("  1. Update User Name");
        System.out.println("  2. Deactivate User");
        System.out.println("  3. Activate User");
        System.out.print("Choose action: ");
        String action = scanner.nextLine().trim();

        System.out.print("User ID: ");
        try {
            int userId = Integer.parseInt(scanner.nextLine().trim());

            switch (action) {
                case "1" -> {
                    System.out.print("New name: ");
                    String newName = scanner.nextLine().trim();
                    // Load user, update name, save
                    var allUsers = reportService.getAllUsers();
                    var optUser = allUsers.stream().filter(u -> u.getId() == userId).findFirst();
                    if (optUser.isEmpty()) { System.out.println("[!] User not found."); return; }
                    User user = optUser.get();
                    user.setName(User.normalizeName(newName));
                    reportService.updateUser(user);
                    System.out.println("✔ User name updated.");
                }
                case "2" -> {
                    reportService.deactivateUser(userId);
                    System.out.println("✔ User ID " + userId + " deactivated.");
                }
                case "3" -> {
                    reportService.activateUser(userId);
                    System.out.println("✔ User ID " + userId + " activated.");
                }
                default -> System.out.println("[!] Invalid action.");
            }

        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter a valid numeric ID.");
        } catch (SQLException e) {
            System.out.println("[Error] Database error: " + e.getMessage());
        }
    }

    private void viewAllResources() {
        System.out.println("\n--- ALL RESOURCES ---");
        try {
            List<Resource> resources = reportService.getAllResources();
            if (resources.isEmpty()) {
                System.out.println("No resources found.");
            } else {
                System.out.printf("%-5s %-25s %-15s %-5s %-5s %-6s %-15s%n",
                        "ID", "Name", "Category", "Total", "Avail", "Active", "Owner");
                System.out.println("-".repeat(80));
                for (Resource r : resources) {
                    System.out.printf("%-5d %-25s %-15s %-5d %-5d %-6s %-15s%n",
                            r.getId(), truncate(r.getName(), 24),
                            truncate(r.getCategory(), 14),
                            r.getTotalQuantity(), r.getAvailableQuantity(),
                            r.isActive() ? "Yes" : "No",
                            truncate(r.getOwnerName(), 15));
                }
            }
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void viewAllBookings() {
        System.out.println("\n--- ALL BOOKINGS ---");
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            if (bookings.isEmpty()) {
                System.out.println("No bookings found.");
            } else {
                System.out.printf("%-5s %-18s %-18s %-12s %-12s %-10s%n",
                        "ID", "Student", "Resource", "Start", "End", "Status");
                System.out.println("-".repeat(78));
                for (Booking b : bookings) {
                    System.out.printf("%-5d %-18s %-18s %-12s %-12s %-10s%n",
                            b.getId(), truncate(b.getStudentName(), 17),
                            truncate(b.getResourceName(), 17),
                            b.getStartDate(), b.getEndDate(), b.getStatus());
                }
            }
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void viewAllTransactions() {
        System.out.println("\n--- ALL TRANSACTIONS ---");
        try {
            List<Transaction> transactions = bookingService.getAllTransactions();
            if (transactions.isEmpty()) {
                System.out.println("No transactions recorded.");
            } else {
                System.out.printf("%-5s %-8s %-18s %-20s %-15s %-12s%n",
                        "ID", "Booking", "Manager", "Issued At", "Returned At", "Condition");
                System.out.println("-".repeat(80));
                for (Transaction t : transactions) {
                    System.out.printf("%-5d %-8d %-18s %-20s %-15s %-12s%n",
                            t.getId(), t.getBookingId(),
                            truncate(t.getManagerName(), 17),
                            t.getIssuedAt(),
                            t.getReturnedAt() != null ? t.getReturnedAt().toString() : "Active",
                            t.getConditionOnReturn() != null ? t.getConditionOnReturn() : "-");
                }
            }
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void viewDamageReports() {
        System.out.println("\n--- DAMAGE REPORTS ---");
        try {
            List<DamageReport> reports = bookingService.getAllDamageReports();
            if (reports.isEmpty()) {
                System.out.println("No damage reports filed.");
            } else {
                System.out.printf("%-5s %-8s %-20s %-10s %-25s%n",
                        "ID", "Resource", "Resource Name", "Severity", "Description");
                System.out.println("-".repeat(70));
                for (DamageReport dr : reports) {
                    System.out.printf("%-5d %-8d %-20s %-10s %-25s%n",
                            dr.getId(), dr.getResourceId(),
                            truncate(dr.getResourceName(), 19),
                            dr.getSeverity(),
                            truncate(dr.getDamageDescription(), 25));
                }
            }
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void viewSystemSummary() {
        try {
            System.out.println(reportService.generateSystemSummary());
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void printUserTable(List<User> users) {
        System.out.printf("%-5s %-22s %-28s %-10s %-6s%n",
                "ID", "Name", "Email", "Role", "Active");
        System.out.println("-".repeat(72));
        for (User u : users) {
            System.out.printf("%-5d %-22s %-28s %-10s %-6s%n",
                    u.getId(), truncate(u.getName(), 21),
                    truncate(u.getEmail(), 27),
                    u.getRole(), u.isActive() ? "Yes" : "No");
        }
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 2) + "..";
    }
}
