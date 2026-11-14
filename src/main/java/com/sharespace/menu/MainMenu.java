package com.sharespace.menu;

import com.sharespace.database.DBConnection;
import com.sharespace.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Application entry point menu.
 * Displays: Register | Login | Exit
 */
public class MainMenu {

    private final Scanner scanner;
    private final StudentMenu studentMenu;
    private final OwnerMenu   ownerMenu;
    private final ManagerMenu managerMenu;
    private final AdminMenu   adminMenu;

    public MainMenu(Scanner scanner) {
        this.scanner     = scanner;
        this.studentMenu = new StudentMenu(scanner);
        this.ownerMenu   = new OwnerMenu(scanner);
        this.managerMenu = new ManagerMenu(scanner);
        this.adminMenu   = new AdminMenu(scanner);
    }

    public void start() {
        // Test DB connection first
        System.out.println("\n[ShareSpace] Connecting to database...");
        if (!DBConnection.testConnection()) {
            System.out.println("[DB] Access denied with default password.");
            System.out.print("Please enter your MySQL root password: ");
            String pwd = scanner.nextLine().trim();
            DBConnection.setPassword(pwd);
            if (!DBConnection.testConnection()) {
                System.out.println("\n[ERROR] Still cannot connect to the database.");
                System.out.println("  Please check:");
                System.out.println("  1. MySQL server is running");
                System.out.println("  2. Database 'sharespace' exists (run database/sharespace.sql)");
                System.out.println("  3. Your MySQL root password");
                System.out.println("\nExiting ShareSpace.");
                return;
            }
        }
        System.out.println("[ShareSpace] Database connected successfully.\n");

        boolean running = true;
        while (running) {
            printHeader();
            System.out.println("  1. Register");
            System.out.println("  2. Login");
            System.out.println("  3. Run Concurrency Demo (Milestone 3 & 4)");
            System.out.println("  4. Exit");
            System.out.println();
            System.out.print("Enter your choice: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> handleRegister();
                case "2" -> handleLogin();
                case "3" -> handleConcurrencyDemo();
                case "4" -> {
                    System.out.println("\nThank you for using ShareSpace. Goodbye!");
                    DBConnection.closeConnection();
                    running = false;
                }
                default  -> System.out.println("\n[!] Invalid choice. Please enter 1, 2, 3 or 4.\n");
            }
        }
    }

    private void printHeader() {
        System.out.println();
        System.out.println("=".repeat(44));
        System.out.println("           SHARESPACE");
        System.out.println("   CAMPUS RESOURCE SHARING SYSTEM");
        System.out.println("=".repeat(44));
    }

    private void handleRegister() {
        System.out.println("\n--- REGISTER ---");
        try {
            com.sharespace.service.AuthService authService = new com.sharespace.service.AuthService();

            System.out.print("Full Name        : ");
            String name = scanner.nextLine().trim();

            System.out.print("Email            : ");
            String email = scanner.nextLine().trim();

            System.out.print("Password (min 6) : ");
            String password = scanner.nextLine().trim();

            System.out.println("Roles: STUDENT | OWNER | MANAGER | ADMIN");
            System.out.print("Role             : ");
            String role = scanner.nextLine().trim().toUpperCase();

            String department = "", yearStr = "1", contact = "", empId = "", adminCode = "";

            switch (role) {
                case "STUDENT" -> {
                    System.out.print("Department       : ");
                    department = scanner.nextLine().trim();
                    System.out.print("Year (1-4)       : ");
                    yearStr = scanner.nextLine().trim();
                }
                case "OWNER" -> {
                    System.out.print("Department       : ");
                    department = scanner.nextLine().trim();
                    System.out.print("Contact Number   : ");
                    contact = scanner.nextLine().trim();
                }
                case "MANAGER" -> {
                    System.out.print("Employee ID      : ");
                    empId = scanner.nextLine().trim();
                }
                case "ADMIN" -> {
                    System.out.print("Admin Code       : ");
                    adminCode = scanner.nextLine().trim();
                }
            }

            User user = authService.register(name, email, password, role,
                    department, yearStr, contact, empId, adminCode);

            System.out.println("\n✔ Registration successful!");
            System.out.println("  Welcome, " + user.getName() + "! Your role: " + user.getRole());
            System.out.println("  You can now log in.\n");

        } catch (com.sharespace.exceptions.InvalidUserException e) {
            System.out.println("\n[Error] Registration failed: " + e.getMessage() + "\n");
        } catch (com.sharespace.exceptions.DuplicateRecordException e) {
            System.out.println("\n[Error] " + e.getMessage() + "\n");
        } catch (SQLException e) {
            System.out.println("\n[Error] Database error: " + e.getMessage() + "\n");
        }
    }

    private void handleLogin() {
        System.out.println("\n--- LOGIN ---");
        try {
            com.sharespace.service.AuthService authService = new com.sharespace.service.AuthService();

            System.out.print("Email    : ");
            String email = scanner.nextLine().trim();

            System.out.print("Password : ");
            String password = scanner.nextLine().trim();

            User user = authService.login(email, password);

            System.out.println("\n✔ Login successful! Welcome back, " + user.getName()
                    + " (" + user.getRole() + ")");

            // Route to the correct role dashboard
            switch (user.getRole()) {
                case "STUDENT" -> studentMenu.show((com.sharespace.model.Student) user);
                case "OWNER"   -> ownerMenu.show((com.sharespace.model.Owner) user);
                case "MANAGER" -> managerMenu.show((com.sharespace.model.Manager) user);
                case "ADMIN"   -> adminMenu.show((com.sharespace.model.Admin) user);
                default        -> System.out.println("[!] Unknown role: " + user.getRole());
            }

        } catch (com.sharespace.exceptions.InvalidUserException e) {
            System.out.println("\n[Error] Login failed: " + e.getMessage() + "\n");
        } catch (SQLException e) {
            System.out.println("\n[Error] Database error: " + e.getMessage() + "\n");
        }
    }

    private void handleConcurrencyDemo() {
        System.out.println("\n--- CONCURRENCY DEMONSTRATIONS ---");
        System.out.println("1. Milestone 3: Concurrent Booking Simulation (5 threads, 2 slots)");
        System.out.println("2. Milestone 4: Producer-Consumer + ExecutorService");
        System.out.println("3. Both");
        System.out.println("4. Back");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> com.sharespace.concurrency.BookingExecutor.runConcurrentBookingSimulation();
            case "2" -> com.sharespace.concurrency.BookingExecutor.runProducerConsumerDemo();
            case "3" -> {
                com.sharespace.concurrency.BookingExecutor.runConcurrentBookingSimulation();
                com.sharespace.concurrency.BookingExecutor.runProducerConsumerDemo();
            }
            default  -> {}
        }
    }
}
