package com.sharespace.menu;

import com.sharespace.model.Resource;
import com.sharespace.model.Booking;
import com.sharespace.model.Student;
import com.sharespace.service.BookingService;
import com.sharespace.service.EligibilityService;
import com.sharespace.service.ResourceService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Student dashboard menu.
 * Options: view/search/filter resources, request booking, view/cancel bookings, profile, logout.
 */
public class StudentMenu {

    private final Scanner scanner;
    private final ResourceService resourceService;
    private final BookingService bookingService;
    private final EligibilityService eligibilityService;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public StudentMenu(Scanner scanner) {
        this.scanner            = scanner;
        this.resourceService    = new ResourceService();
        this.bookingService     = new BookingService();
        this.eligibilityService = new EligibilityService();
    }

    public void show(Student student) {
        boolean running = true;
        while (running) {
            printDashboard(student.getName());
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1"  -> viewAvailableResources();
                case "2"  -> searchResources();
                case "3"  -> filterByCategory();
                case "4"  -> viewResourceDetails();
                case "5"  -> requestBooking(student);
                case "6"  -> viewMyBookings(student);
                case "7"  -> checkBookingStatus(student);
                case "8"  -> cancelBooking(student);
                case "9"  -> viewBookingHistory(student);
                case "10" -> viewProfile(student);
                case "11" -> { System.out.println("\n[Student] Logged out successfully.\n"); running = false; }
                default   -> System.out.println("\n[!] Invalid choice.\n");
            }
        }
    }

    private void printDashboard(String name) {
        System.out.println("\n" + "=".repeat(44));
        System.out.println("  STUDENT DASHBOARD — " + name);
        System.out.println("=".repeat(44));
        System.out.println("  1.  View Available Resources");
        System.out.println("  2.  Search Resources");
        System.out.println("  3.  Filter Resources by Category");
        System.out.println("  4.  View Resource Details");
        System.out.println("  5.  Request Resource Booking");
        System.out.println("  6.  View My Booking Requests");
        System.out.println("  7.  Check Booking Status");
        System.out.println("  8.  Cancel Eligible Booking");
        System.out.println("  9.  View Booking History");
        System.out.println("  10. View My Profile");
        System.out.println("  11. Logout");
        System.out.println("=".repeat(44));
    }

    private void viewAvailableResources() {
        System.out.println("\n--- AVAILABLE RESOURCES ---");
        try {
            List<Resource> resources = resourceService.getAvailableResources();
            if (resources.isEmpty()) {
                System.out.println("No resources are currently available.");
                return;
            }
            printResourceTable(resources);
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void searchResources() {
        System.out.println("\n--- SEARCH RESOURCES ---");
        System.out.print("Enter keyword: ");
        String keyword = scanner.nextLine().trim();
        if (keyword.isEmpty()) { System.out.println("[!] Keyword cannot be empty."); return; }
        try {
            List<Resource> results = resourceService.searchByName(keyword);
            if (results.isEmpty()) {
                System.out.println("No resources found matching: " + keyword);
            } else {
                printResourceTable(results);
            }
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void filterByCategory() {
        System.out.println("\n--- FILTER BY CATEGORY ---");
        System.out.println("Common categories: LAPTOP, CAMERA, PROJECTOR, LAB_EQUIPMENT, SEMINAR_ROOM, OTHER");
        System.out.print("Enter category: ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) { System.out.println("[!] Category cannot be empty."); return; }
        try {
            List<Resource> results = resourceService.filterByCategory(category);
            if (results.isEmpty()) {
                System.out.println("No resources found in category: " + category);
            } else {
                printResourceTable(results);
            }
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void viewResourceDetails() {
        System.out.println("\n--- VIEW RESOURCE DETAILS ---");
        System.out.print("Enter Resource ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Optional<Resource> opt = resourceService.findById(id);
            if (opt.isEmpty()) {
                System.out.println("[!] Resource not found with ID: " + id);
            } else {
                System.out.println(opt.get().generateResourceReport());
            }
        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter a valid numeric ID.");
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void requestBooking(Student student) {
        System.out.println("\n--- REQUEST RESOURCE BOOKING ---");
        try {
            System.out.print("Resource ID       : ");
            int resourceId = Integer.parseInt(scanner.nextLine().trim());

            Optional<Resource> optRes = resourceService.findById(resourceId);
            if (optRes.isEmpty() || !optRes.get().isActive()) {
                System.out.println("[!] Resource not found or inactive."); return;
            }
            Resource resource = optRes.get();

            // Eligibility check (Milestone 1 — Eligible interface)
            String eligibilityMsg = eligibilityService.getEligibilityMessage(student, resource);
            System.out.println("[Eligibility] " + eligibilityMsg);
            if (!eligibilityService.isEligible(student, resource)) {
                System.out.println("[!] You are not eligible to book this resource."); return;
            }

            System.out.print("Start Date (yyyy-MM-dd): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine().trim(), DATE_FMT);

            System.out.print("End Date   (yyyy-MM-dd): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine().trim(), DATE_FMT);

            System.out.print("Quantity Requested : ");
            int qty = Integer.parseInt(scanner.nextLine().trim());

            Booking booking = bookingService.createBookingRequest(
                    student.getId(), student.getName(), resourceId, startDate, endDate, qty);

            System.out.println("\n✔ Booking request submitted successfully!");
            System.out.println(booking.generateBookingReceipt());

        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter valid numeric values.");
        } catch (DateTimeParseException e) {
            System.out.println("[!] Invalid date format. Use yyyy-MM-dd (e.g., 2024-12-01).");
        } catch (com.sharespace.exceptions.BookingException e) {
            System.out.println("[Error] Booking failed: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[Error] Database error: " + e.getMessage());
        }
    }

    private void viewMyBookings(Student student) {
        System.out.println("\n--- MY BOOKING REQUESTS ---");
        try {
            List<Booking> bookings = bookingService.getStudentBookings(student.getId());
            if (bookings.isEmpty()) {
                System.out.println("You have no booking requests.");
            } else {
                printBookingTable(bookings);
            }
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void checkBookingStatus(Student student) {
        System.out.println("\n--- CHECK BOOKING STATUS ---");
        System.out.print("Enter Booking ID: ");
        try {
            int bookingId = Integer.parseInt(scanner.nextLine().trim());
            Optional<Booking> opt = bookingService.findBookingById(bookingId);
            if (opt.isEmpty() || opt.get().getStudentId() != student.getId()) {
                System.out.println("[!] Booking not found or does not belong to you.");
            } else {
                Booking b = opt.get();
                System.out.println("\n  Booking ID   : " + b.getId());
                System.out.println("  Resource     : " + b.getResourceName());
                System.out.println("  Period       : " + b.getStartDate() + " → " + b.getEndDate());
                System.out.println("  Status       : " + b.getStatus());
                System.out.println("  Remarks      : " + (b.getRemarks() != null ? b.getRemarks() : "-"));
            }
        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter a valid numeric ID.");
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void cancelBooking(Student student) {
        System.out.println("\n--- CANCEL BOOKING ---");
        System.out.println("Note: Only PENDING bookings can be cancelled.");
        System.out.print("Enter Booking ID to cancel: ");
        try {
            int bookingId = Integer.parseInt(scanner.nextLine().trim());
            bookingService.cancelBooking(bookingId, student.getId());
            System.out.println("✔ Booking #" + bookingId + " cancelled successfully.");
        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter a valid numeric ID.");
        } catch (com.sharespace.exceptions.BookingException e) {
            System.out.println("[Error] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[Error] Database error: " + e.getMessage());
        }
    }

    private void viewBookingHistory(Student student) {
        System.out.println("\n--- BOOKING HISTORY ---");
        try {
            List<Booking> bookings = bookingService.getStudentBookings(student.getId());
            List<Booking> history = bookings.stream()
                    .filter(b -> List.of("RETURNED","REJECTED","CANCELLED").contains(b.getStatus()))
                    .toList();
            if (history.isEmpty()) {
                System.out.println("No completed bookings in history.");
            } else {
                printBookingTable(history);
            }
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void viewProfile(Student student) {
        System.out.println("\n--- MY PROFILE ---");
        System.out.println(student.generateProfileReport());
    }

    // ---- Utility display methods ----

    private void printResourceTable(List<Resource> resources) {
        System.out.printf("%-5s %-25s %-15s %-5s %-20s%n",
                "ID", "Name", "Category", "Avail", "Location");
        System.out.println("-".repeat(72));
        for (Resource r : resources) {
            System.out.printf("%-5d %-25s %-15s %-5d %-20s%n",
                    r.getId(), truncate(r.getName(), 24),
                    truncate(r.getCategory(), 14),
                    r.getAvailableQuantity(),
                    truncate(r.getLocation(), 20));
        }
    }

    private void printBookingTable(List<Booking> bookings) {
        System.out.printf("%-5s %-20s %-12s %-12s %-10s%n",
                "ID", "Resource", "Start", "End", "Status");
        System.out.println("-".repeat(62));
        for (Booking b : bookings) {
            System.out.printf("%-5d %-20s %-12s %-12s %-10s%n",
                    b.getId(), truncate(b.getResourceName(), 19),
                    b.getStartDate(), b.getEndDate(), b.getStatus());
        }
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 2) + "..";
    }
}
