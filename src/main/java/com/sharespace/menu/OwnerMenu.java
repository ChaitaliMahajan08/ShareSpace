package com.sharespace.menu;

import com.sharespace.exceptions.BookingException;
import com.sharespace.exceptions.InvalidResourceException;
import com.sharespace.model.Booking;
import com.sharespace.model.Owner;
import com.sharespace.model.Resource;
import com.sharespace.service.BookingService;
import com.sharespace.service.ResourceService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Owner dashboard menu.
 * Options: add/update/remove resources, view & manage booking requests.
 */
public class OwnerMenu {

    private final Scanner scanner;
    private final ResourceService resourceService;
    private final BookingService  bookingService;

    public OwnerMenu(Scanner scanner) {
        this.scanner         = scanner;
        this.resourceService = new ResourceService();
        this.bookingService  = new BookingService();
    }

    public void show(Owner owner) {
        boolean running = true;
        while (running) {
            printDashboard(owner.getName());
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1"  -> addResource(owner);
                case "2"  -> viewMyResources(owner);
                case "3"  -> updateResource(owner);
                case "4"  -> removeResource(owner);
                case "5"  -> viewIncomingRequests(owner);
                case "6"  -> approveBooking(owner);
                case "7"  -> rejectBooking(owner);
                case "8"  -> viewResourceBookingHistory(owner);
                case "9"  -> viewProfile(owner);
                case "10" -> { System.out.println("\n[Owner] Logged out successfully.\n"); running = false; }
                default   -> System.out.println("\n[!] Invalid choice.\n");
            }
        }
    }

    private void printDashboard(String name) {
        System.out.println("\n" + "=".repeat(44));
        System.out.println("  OWNER DASHBOARD — " + name);
        System.out.println("=".repeat(44));
        System.out.println("  1.  Add Resource");
        System.out.println("  2.  View My Listed Resources");
        System.out.println("  3.  Update Resource Details");
        System.out.println("  4.  Remove Resource Listing");
        System.out.println("  5.  View Incoming Booking Requests");
        System.out.println("  6.  Approve Booking Request");
        System.out.println("  7.  Reject Booking Request");
        System.out.println("  8.  View Resource Booking History");
        System.out.println("  9.  View My Profile");
        System.out.println("  10. Logout");
        System.out.println("=".repeat(44));
    }

    private void addResource(Owner owner) {
        System.out.println("\n--- ADD RESOURCE ---");
        try {
            System.out.print("Resource Name : ");
            String name = scanner.nextLine().trim();

            System.out.println("Categories: LAPTOP, CAMERA, PROJECTOR, LAB_EQUIPMENT, SEMINAR_ROOM, OTHER");
            System.out.print("Category      : ");
            String category = scanner.nextLine().trim();

            System.out.print("Description   : ");
            String description = scanner.nextLine().trim();

            System.out.print("Quantity      : ");
            int quantity = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Location      : ");
            String location = scanner.nextLine().trim();

            Resource resource = resourceService.addResource(
                    name, category, description, quantity, location,
                    owner.getId(), owner.getName());

            System.out.println("\n✔ Resource added successfully! ID: " + resource.getId());
            System.out.println(resource.generateResourceReport());

        } catch (NumberFormatException e) {
            System.out.println("[!] Quantity must be a valid number.");
        } catch (InvalidResourceException e) {
            System.out.println("[Error] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[Error] Database error: " + e.getMessage());
        }
    }

    private void viewMyResources(Owner owner) {
        System.out.println("\n--- MY LISTED RESOURCES ---");
        try {
            List<Resource> resources = resourceService.getResourcesByOwner(owner.getId());
            if (resources.isEmpty()) {
                System.out.println("You have not listed any resources yet.");
            } else {
                printResourceTable(resources);
            }
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void updateResource(Owner owner) {
        System.out.println("\n--- UPDATE RESOURCE ---");
        try {
            System.out.print("Resource ID to update: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            Optional<Resource> opt = resourceService.findById(id);
            if (opt.isEmpty()) { System.out.println("[!] Resource not found."); return; }
            Resource resource = opt.get();

            if (resource.getOwnerId() != owner.getId()) {
                System.out.println("[!] You can only update your own resources."); return;
            }

            System.out.println("Current Name: " + resource.getName());
            System.out.print("New Name (Enter to keep): ");
            String newName = scanner.nextLine().trim();
            if (!newName.isEmpty()) resource.setName(newName);

            System.out.println("Current Description: " + resource.getDescription());
            System.out.print("New Description (Enter to keep): ");
            String newDesc = scanner.nextLine().trim();
            if (!newDesc.isEmpty()) resource.setDescription(newDesc);

            System.out.println("Current Location: " + resource.getLocation());
            System.out.print("New Location (Enter to keep): ");
            String newLoc = scanner.nextLine().trim();
            if (!newLoc.isEmpty()) resource.setLocation(newLoc);

            System.out.println("Current Total Quantity: " + resource.getTotalQuantity());
            System.out.print("New Total Quantity (Enter to keep): ");
            String qtyStr = scanner.nextLine().trim();
            if (!qtyStr.isEmpty()) {
                int newQty = Integer.parseInt(qtyStr);
                resource.setTotalQuantity(newQty);
                resource.setAvailableQuantity(newQty);
            }

            resourceService.updateResource(resource);
            System.out.println("✔ Resource updated successfully.");

        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter valid numeric values.");
        } catch (InvalidResourceException e) {
            System.out.println("[Error] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[Error] Database error: " + e.getMessage());
        }
    }

    private void removeResource(Owner owner) {
        System.out.println("\n--- REMOVE RESOURCE ---");
        try {
            System.out.print("Resource ID to remove: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            Optional<Resource> opt = resourceService.findById(id);
            if (opt.isEmpty()) { System.out.println("[!] Resource not found."); return; }
            if (opt.get().getOwnerId() != owner.getId()) {
                System.out.println("[!] You can only remove your own resources."); return;
            }

            System.out.print("Are you sure? (yes/no): ");
            if ("yes".equalsIgnoreCase(scanner.nextLine().trim())) {
                resourceService.removeResource(id);
                System.out.println("✔ Resource removed (deactivated) successfully.");
            } else {
                System.out.println("Removal cancelled.");
            }

        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter a valid ID.");
        } catch (SQLException e) {
            System.out.println("[Error] Database error: " + e.getMessage());
        }
    }

    private void viewIncomingRequests(Owner owner) {
        System.out.println("\n--- INCOMING BOOKING REQUESTS ---");
        try {
            List<Booking> bookings = bookingService.getPendingBookingsForOwner(owner.getId());
            if (bookings.isEmpty()) {
                System.out.println("No pending booking requests for your resources.");
            } else {
                printBookingTable(bookings);
            }
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void approveBooking(Owner owner) {
        System.out.println("\n--- APPROVE BOOKING ---");
        try {
            System.out.print("Booking ID to approve: ");
            int bookingId = Integer.parseInt(scanner.nextLine().trim());

            bookingService.approveBooking(bookingId, owner.getId());
            System.out.println("✔ Booking #" + bookingId + " approved successfully.");
            System.out.println("  The manager can now issue this resource.");

        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter a valid numeric ID.");
        } catch (BookingException e) {
            System.out.println("[Error] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[Error] Database error: " + e.getMessage());
        }
    }

    private void rejectBooking(Owner owner) {
        System.out.println("\n--- REJECT BOOKING ---");
        try {
            System.out.print("Booking ID to reject : ");
            int bookingId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Reason for rejection : ");
            String remarks = scanner.nextLine().trim();

            bookingService.rejectBooking(bookingId, owner.getId(), remarks);
            System.out.println("✔ Booking #" + bookingId + " rejected.");

        } catch (NumberFormatException e) {
            System.out.println("[!] Please enter a valid numeric ID.");
        } catch (BookingException e) {
            System.out.println("[Error] " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("[Error] Database error: " + e.getMessage());
        }
    }

    private void viewResourceBookingHistory(Owner owner) {
        System.out.println("\n--- RESOURCE BOOKING HISTORY ---");
        try {
            List<Booking> bookings = bookingService.getBookingsByOwnerResources(owner.getId());
            if (bookings.isEmpty()) {
                System.out.println("No booking history for your resources.");
            } else {
                printBookingTable(bookings);
            }
        } catch (SQLException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void viewProfile(Owner owner) {
        System.out.println("\n--- MY PROFILE ---");
        System.out.println(owner.generateProfileReport());
    }

    private void printResourceTable(List<Resource> resources) {
        System.out.printf("%-5s %-25s %-15s %-5s %-5s %-15s%n",
                "ID", "Name", "Category", "Total", "Avail", "Location");
        System.out.println("-".repeat(72));
        for (Resource r : resources) {
            System.out.printf("%-5d %-25s %-15s %-5d %-5d %-15s%n",
                    r.getId(), truncate(r.getName(), 24),
                    truncate(r.getCategory(), 14),
                    r.getTotalQuantity(), r.getAvailableQuantity(),
                    truncate(r.getLocation(), 15));
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
