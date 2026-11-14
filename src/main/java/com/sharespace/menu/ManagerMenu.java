package com.sharespace.menu;

import com.sharespace.exceptions.BookingException;
import com.sharespace.model.Booking;
import com.sharespace.model.DamageReport;
import com.sharespace.model.Manager;
import com.sharespace.model.Transaction;
import com.sharespace.service.BookingService;
import com.sharespace.service.ResourceService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Manager dashboard menu.
 * Handles physical issue and return of approved resources.
 */
public class ManagerMenu {

    private final Scanner scanner;
    private final BookingService  bookingService;
    private final ResourceService resourceService;

    public ManagerMenu(Scanner scanner) {
        this.scanner         = scanner;
        this.bookingService  = new BookingService();
        this.resourceService = new ResourceService();
    }

    public void show(Manager manager) {
        boolean running = true;
        while (running) {
            printDashboard(manager.getName());
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> viewApprovedBookings();
                case "2" -> verifyBooking();
                case "3" -> issueResource(manager);
                case "4" -> recordReturn(manager);
                case "5" -> viewCurrentlyIssued();
                case "6" -> checkResourceCondition();
                case "7" -> reportDamage(manager);
                case "8" -> viewTransactionHistory();
                case "9" -> { System.out.println("\n[Manager] Logged out successfully.\n"); running = false; }
                default  -> System.out.println("\n[!] Invalid choice.\n");
            }
        }
    }

    private void printDashboard(String name) {
        System.out.println("\n" + "=".repeat(44));
        System.out.println("  MANAGER DASHBOARD — " + name);
        System.out.println("=".repeat(44));
        System.out.println("  1. View Approved Bookings");
        System.out.println("  2. Verify Booking and Borrower");
        System.out.println("  3. Issue Resource");
        System.out.println("  4. Record Resource Return");
        System.out.println("  5. View Currently Issued Resources");
        System.out.println("  6. Check Resource Condition");
        System.out.println("  7. Report Damaged Resource");
        System.out.println("  8. View Transaction History");
        System.out.println("  9. Logout");
        System.out.println("=".repeat(44));
    }

    private void viewApprovedBookings() {
        System.out.println("\n--- APPROVED BOOKINGS (Ready to Issue) ---");
        try {
            List<Booking> bookings = bookingService.getApprovedBookings();
            if (bookings.isEmpty()) {
                System.out.println("No approved bookings waiting to be issued.");
            } else {
                printBookingTable(bookings);
            }
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void verifyBooking() {
        System.out.println("\n--- VERIFY BOOKING AND BORROWER ---");
        System.out.print("Enter Booking ID: ");
        try {
            int bookingId = Integer.parseInt(scanner.nextLine().trim());
            Optional<Booking> opt = bookingService.findBookingById(bookingId);
            if (opt.isEmpty()) {
                System.out.println("[!] Booking not found.");
                return;
            }
            Booking b = opt.get();
            System.out.println("\n  Booking ID     : " + b.getId());
            System.out.println("  Student        : " + b.getStudentName() + " (ID: " + b.getStudentId() + ")");
            System.out.println("  Resource       : " + b.getResourceName() + " (ID: " + b.getResourceId() + ")");
            System.out.println("  Quantity       : " + b.getQuantityRequested());
            System.out.println("  Period         : " + b.getStartDate() + " → " + b.getEndDate());
            System.out.println("  Status         : " + b.getStatus());
            System.out.println("  Remarks        : " + (b.getRemarks() != null ? b.getRemarks() : "-"));
            System.out.println("  Created        : " + b.getCreatedAt());

            if ("APPROVED".equals(b.getStatus())) {
                System.out.println("\n  ✔ This booking is APPROVED and ready to be issued.");
            } else {
                System.out.println("\n  [!] Cannot issue — current status is: " + b.getStatus());
            }
        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter a valid numeric ID.");
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void issueResource(Manager manager) {
        System.out.println("\n--- ISSUE RESOURCE ---");
        System.out.println("Note: Only APPROVED bookings can be issued.");
        System.out.print("Booking ID to issue: ");
        try {
            int bookingId = Integer.parseInt(scanner.nextLine().trim());

            Transaction transaction = bookingService.issueResource(
                    bookingId, manager.getId(), manager.getName());

            System.out.println("\n✔ Resource issued successfully!");
            System.out.println("  Transaction ID : " + transaction.getId());
            System.out.println("  Issued At      : " + transaction.getIssuedAt());
            System.out.println("  Issued By      : " + manager.getName());
            System.out.println("\n  Remind the borrower of their return date.");

        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter a valid numeric ID.");
        } catch (BookingException e) {
            System.out.println("[Error] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[Error] Database error: " + e.getMessage());
        }
    }

    private void recordReturn(Manager manager) {
        System.out.println("\n--- RECORD RESOURCE RETURN ---");
        System.out.print("Transaction ID: ");
        try {
            int transactionId = Integer.parseInt(scanner.nextLine().trim());

            System.out.println("Condition options: GOOD, MINOR_DAMAGE, DAMAGED");
            System.out.print("Condition on return: ");
            String condition = scanner.nextLine().trim().toUpperCase();

            System.out.print("Additional notes   : ");
            String notes = scanner.nextLine().trim();

            bookingService.returnResource(transactionId, manager.getId(), condition, notes);

            System.out.println("\n✔ Return recorded successfully!");
            if ("DAMAGED".equals(condition)) {
                System.out.println("  ⚠ Resource returned DAMAGED — availability NOT restored.");
                System.out.println("  Use option 7 to file a damage report.");
            } else {
                System.out.println("  Resource availability has been restored.");
            }

        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter a valid numeric ID.");
        } catch (BookingException e) {
            System.out.println("[Error] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[Error] Database error: " + e.getMessage());
        }
    }

    private void viewCurrentlyIssued() {
        System.out.println("\n--- CURRENTLY ISSUED RESOURCES ---");
        try {
            List<Transaction> transactions = bookingService.getCurrentlyIssuedTransactions();
            if (transactions.isEmpty()) {
                System.out.println("No resources are currently issued.");
            } else {
                System.out.printf("%-5s %-8s %-20s %-20s%n",
                        "TxID", "BookingID", "Manager", "Issued At");
                System.out.println("-".repeat(55));
                for (Transaction t : transactions) {
                    System.out.printf("%-5d %-8d %-20s %-20s%n",
                            t.getId(), t.getBookingId(),
                            truncate(t.getManagerName(), 19),
                            t.getIssuedAt());
                }
            }
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void checkResourceCondition() {
        System.out.println("\n--- CHECK RESOURCE CONDITION ---");
        System.out.print("Enter Resource ID: ");
        try {
            int resourceId = Integer.parseInt(scanner.nextLine().trim());
            var opt = resourceService.findById(resourceId);
            if (opt.isEmpty()) {
                System.out.println("[!] Resource not found.");
                return;
            }
            var resource = opt.get();
            System.out.println("\n  Resource    : " + resource.getName());
            System.out.println("  Category    : " + resource.getCategory());
            System.out.println("  Total Qty   : " + resource.getTotalQuantity());
            System.out.println("  Available   : " + resource.getAvailableQuantity());
            System.out.println("  Active      : " + (resource.isActive() ? "Yes" : "No"));
            System.out.println("  Location    : " + resource.getLocation());

            int inUse = resource.getTotalQuantity() - resource.getAvailableQuantity();
            System.out.println("  Currently in use: " + inUse);

        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter a valid numeric ID.");
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void reportDamage(Manager manager) {
        System.out.println("\n--- REPORT DAMAGED RESOURCE ---");
        try {
            System.out.print("Transaction ID     : ");
            int transactionId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Resource ID        : ");
            int resourceId = Integer.parseInt(scanner.nextLine().trim());

            var optRes = resourceService.findById(resourceId);
            String resourceName = optRes.map(com.sharespace.model.Resource::getName).orElse("Unknown");

            System.out.print("Damage Description : ");
            String description = scanner.nextLine().trim();

            System.out.println("Severity options: MINOR, MODERATE, SEVERE");
            System.out.print("Severity           : ");
            String severity = scanner.nextLine().trim().toUpperCase();

            if (!List.of("MINOR","MODERATE","SEVERE").contains(severity)) {
                System.out.println("[!] Invalid severity. Use MINOR, MODERATE, or SEVERE.");
                return;
            }

            DamageReport report = new DamageReport();
            report.setTransactionId(transactionId);
            report.setResourceId(resourceId);
            report.setResourceName(resourceName);
            report.setDamageDescription(description);
            report.setSeverity(severity);
            report.setReportedByManagerId(manager.getId());
            report.setReportedAt(LocalDateTime.now());

            int reportId = bookingService.createDamageReport(report);
            System.out.println("✔ Damage report filed. Report ID: " + reportId);

        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter valid numeric IDs.");
        } catch (SQLException e) {
            System.out.println("[Error] Database error: " + e.getMessage());
        }
    }

    private void viewTransactionHistory() {
        System.out.println("\n--- TRANSACTION HISTORY ---");
        try {
            List<Transaction> transactions = bookingService.getAllTransactions();
            if (transactions.isEmpty()) {
                System.out.println("No transactions recorded yet.");
            } else {
                System.out.printf("%-5s %-8s %-20s %-20s %-15s%n",
                        "ID", "Booking", "Manager", "Issued At", "Returned At");
                System.out.println("-".repeat(70));
                for (Transaction t : transactions) {
                    System.out.printf("%-5d %-8d %-20s %-20s %-15s%n",
                            t.getId(), t.getBookingId(),
                            truncate(t.getManagerName(), 19),
                            t.getIssuedAt(),
                            t.getReturnedAt() != null ? t.getReturnedAt().toString() : "Not returned");
                }
            }
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void printBookingTable(List<Booking> bookings) {
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

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 2) + "..";
    }
}
