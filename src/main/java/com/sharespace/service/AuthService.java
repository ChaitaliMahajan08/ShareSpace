package com.sharespace.service;

import com.sharespace.dao.UserDAO;
import com.sharespace.exceptions.DuplicateRecordException;
import com.sharespace.exceptions.InvalidUserException;
import com.sharespace.model.*;
import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Registers a new user after validation.
     * throws InvalidUserException if input is invalid
     * throws DuplicateRecordException if email already exists
     */
    public User register(String name, String email, String password, String role,
                         String department, String yearStr, String contactNumber,
                         String employeeId, String adminCode)
            throws InvalidUserException, DuplicateRecordException, SQLException {

        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidUserException("Name cannot be empty.");
        }
        // Validate email using User static method
        if (!User.validateEmail(email)) {
            throw new InvalidUserException("Invalid email format: " + email);
        }
        // Validate password
        if (password == null || password.length() < 6) {
            throw new InvalidUserException("Password must be at least 6 characters.");
        }
        // Validate role
        if (!role.equalsIgnoreCase("STUDENT") && !role.equalsIgnoreCase("OWNER")
                && !role.equalsIgnoreCase("MANAGER") && !role.equalsIgnoreCase("ADMIN")) {
            throw new InvalidUserException("Invalid role: " + role + ". Must be STUDENT, OWNER, MANAGER, or ADMIN.");
        }

        String normalizedEmail = User.normalizeEmail(email);
        String normalizedName = User.normalizeName(name);

        // Check duplicate email
        if (userDAO.emailExists(normalizedEmail)) {
            throw new DuplicateRecordException("Email already registered: " + normalizedEmail);
        }

        // Create the appropriate User subclass
        User user;
        String upperRole = role.toUpperCase();
        switch (upperRole) {
            case "STUDENT" -> {
                Student s = new Student();
                s.setName(normalizedName);
                s.setEmail(normalizedEmail);
                s.setPassword(password);
                s.setRole("STUDENT");
                s.setActive(true);
                s.setDepartment(department != null ? department.trim() : "");
                Integer yr = Student.parseYear(yearStr);
                s.setYear(yr != null ? yr : 1);
                user = s;
            }
            case "OWNER" -> {
                Owner o = new Owner();
                o.setName(normalizedName);
                o.setEmail(normalizedEmail);
                o.setPassword(password);
                o.setRole("OWNER");
                o.setActive(true);
                o.setDepartment(department != null ? department.trim() : "");
                o.setContactNumber(contactNumber != null ? contactNumber.trim() : "");
                user = o;
            }
            case "MANAGER" -> {
                Manager m = new Manager();
                m.setName(normalizedName);
                m.setEmail(normalizedEmail);
                m.setPassword(password);
                m.setRole("MANAGER");
                m.setActive(true);
                m.setEmployeeId(employeeId != null ? employeeId.trim() : "");
                user = m;
            }
            case "ADMIN" -> {
                Admin a = new Admin();
                a.setName(normalizedName);
                a.setEmail(normalizedEmail);
                a.setPassword(password);
                a.setRole("ADMIN");
                a.setActive(true);
                a.setAdminCode(adminCode != null ? adminCode.trim() : "");
                user = a;
            }
            default -> throw new InvalidUserException("Unknown role: " + role);
        }

        boolean success = userDAO.registerUser(user);
        if (!success) {
            throw new SQLException("Failed to register user in database.");
        }

        // Fetch back the user with generated ID
        Optional<User> saved = userDAO.findByEmail(normalizedEmail);
        return saved.orElse(user);
    }

    /**
     * Logs in a user by email and password.
     * throws InvalidUserException if credentials are wrong or account is inactive.
     */
    public User login(String email, String password)
            throws InvalidUserException, SQLException {
        if (!User.validateEmail(email)) {
            throw new InvalidUserException("Invalid email format.");
        }
        String normalizedEmail = User.normalizeEmail(email);
        Optional<User> optUser = userDAO.findByEmail(normalizedEmail);
        if (optUser.isEmpty()) {
            throw new InvalidUserException("No account found with email: " + normalizedEmail);
        }
        User user = optUser.get();
        if (!user.isActive()) {
            throw new InvalidUserException("Account is deactivated. Contact administrator.");
        }
        if (!user.getPassword().equals(password)) {
            throw new InvalidUserException("Incorrect password.");
        }
        return user;
    }
}
