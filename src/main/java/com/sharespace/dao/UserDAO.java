package com.sharespace.dao;

import com.sharespace.database.DBConnection;
import com.sharespace.model.Admin;
import com.sharespace.model.Manager;
import com.sharespace.model.Owner;
import com.sharespace.model.Student;
import com.sharespace.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for User entities.
 * Provides CRUD operations and lookup methods for the users table.
 */
public class UserDAO {

    // -------------------------------------------------------------------------
    // INSERT
    // -------------------------------------------------------------------------

    /**
     * Registers a new user in the database.
     *
     * @param user the User object (Student / Owner / Manager / Admin) to persist
     * @return true if at least one row was inserted
     * @throws SQLException on any database error
     */
    public boolean registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users "
                + "(name, email, password, role, department, year_of_study, "
                + " contact_number, employee_id, admin_code, active) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());

            // Role-specific fields
            if (user instanceof Student) {
                Student s = (Student) user;
                ps.setString(5, s.getDepartment());
                ps.setObject(6, s.getYearOfStudy());
                ps.setString(7, s.getContactNumber());
                ps.setNull(8, java.sql.Types.VARCHAR);
                ps.setNull(9, java.sql.Types.VARCHAR);
            } else if (user instanceof Owner) {
                Owner o = (Owner) user;
                ps.setString(5, o.getDepartment());
                ps.setNull(6, java.sql.Types.INTEGER);
                ps.setString(7, o.getContactNumber());
                ps.setString(8, o.getEmployeeId());
                ps.setNull(9, java.sql.Types.VARCHAR);
            } else if (user instanceof Manager) {
                Manager m = (Manager) user;
                ps.setString(5, m.getDepartment());
                ps.setNull(6, java.sql.Types.INTEGER);
                ps.setString(7, m.getContactNumber());
                ps.setString(8, m.getEmployeeId());
                ps.setNull(9, java.sql.Types.VARCHAR);
            } else if (user instanceof Admin) {
                Admin a = (Admin) user;
                ps.setNull(5, java.sql.Types.VARCHAR);
                ps.setNull(6, java.sql.Types.INTEGER);
                ps.setNull(7, java.sql.Types.VARCHAR);
                ps.setNull(8, java.sql.Types.VARCHAR);
                ps.setString(9, a.getAdminCode());
            } else {
                ps.setNull(5, java.sql.Types.VARCHAR);
                ps.setNull(6, java.sql.Types.INTEGER);
                ps.setNull(7, java.sql.Types.VARCHAR);
                ps.setNull(8, java.sql.Types.VARCHAR);
                ps.setNull(9, java.sql.Types.VARCHAR);
            }

            ps.setBoolean(10, user.isActive());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // SELECT
    // -------------------------------------------------------------------------

    /**
     * Finds a user by their email address.
     *
     * @param email the email to search for
     * @return Optional containing the User if found, empty otherwise
     * @throws SQLException on any database error
     */
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Finds a user by their primary key id.
     *
     * @param id the user id
     * @return Optional containing the User if found, empty otherwise
     * @throws SQLException on any database error
     */
    public Optional<User> findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Returns all users in the system.
     *
     * @return list of all User objects
     * @throws SQLException on any database error
     */
    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY id ASC";
        List<User> users = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    /**
     * Returns all users that match a given role.
     *
     * @param role one of STUDENT, OWNER, MANAGER, ADMIN
     * @return list of matching User objects
     * @throws SQLException on any database error
     */
    public List<User> findByRole(String role) throws SQLException {
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY id ASC";
        List<User> users = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, role.toUpperCase());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        return users;
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    /**
     * Updates the mutable fields of an existing user.
     *
     * @param user the User with updated fields; id must match an existing row
     * @return true if at least one row was updated
     * @throws SQLException on any database error
     */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, password = ?, "
                + "department = ?, year_of_study = ?, contact_number = ?, "
                + "employee_id = ?, admin_code = ?, active = ? "
                + "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());

            if (user instanceof Student) {
                Student s = (Student) user;
                ps.setString(4, s.getDepartment());
                ps.setObject(5, s.getYearOfStudy());
                ps.setString(6, s.getContactNumber());
                ps.setNull(7, java.sql.Types.VARCHAR);
                ps.setNull(8, java.sql.Types.VARCHAR);
            } else if (user instanceof Owner) {
                Owner o = (Owner) user;
                ps.setString(4, o.getDepartment());
                ps.setNull(5, java.sql.Types.INTEGER);
                ps.setString(6, o.getContactNumber());
                ps.setString(7, o.getEmployeeId());
                ps.setNull(8, java.sql.Types.VARCHAR);
            } else if (user instanceof Manager) {
                Manager m = (Manager) user;
                ps.setString(4, m.getDepartment());
                ps.setNull(5, java.sql.Types.INTEGER);
                ps.setString(6, m.getContactNumber());
                ps.setString(7, m.getEmployeeId());
                ps.setNull(8, java.sql.Types.VARCHAR);
            } else if (user instanceof Admin) {
                Admin a = (Admin) user;
                ps.setNull(4, java.sql.Types.VARCHAR);
                ps.setNull(5, java.sql.Types.INTEGER);
                ps.setNull(6, java.sql.Types.VARCHAR);
                ps.setNull(7, java.sql.Types.VARCHAR);
                ps.setString(8, a.getAdminCode());
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
                ps.setNull(5, java.sql.Types.INTEGER);
                ps.setNull(6, java.sql.Types.VARCHAR);
                ps.setNull(7, java.sql.Types.VARCHAR);
                ps.setNull(8, java.sql.Types.VARCHAR);
            }

            ps.setBoolean(9, user.isActive());
            ps.setInt(10, user.getId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deactivates (soft-deletes) a user account.
     *
     * @param userId the id of the user to deactivate
     * @return true if at least one row was updated
     * @throws SQLException on any database error
     */
    public boolean deactivateUser(int userId) throws SQLException {
        String sql = "UPDATE users SET active = FALSE WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Activates a previously deactivated user account.
     *
     * @param userId the id of the user to activate
     * @return true if at least one row was updated
     * @throws SQLException on any database error
     */
    public boolean activateUser(int userId) throws SQLException {
        String sql = "UPDATE users SET active = TRUE WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    // -------------------------------------------------------------------------
    // EXISTENCE CHECK
    // -------------------------------------------------------------------------

    /**
     * Checks whether a given email already exists in the database.
     *
     * @param email the email address to check
     * @return true if the email is already registered
     * @throws SQLException on any database error
     */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

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
     * Maps the current row of a ResultSet to the appropriate User subclass.
     *
     * @param rs the ResultSet positioned at the row to map
     * @return a fully-populated User subclass instance
     * @throws SQLException on any column access error
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        int id               = rs.getInt("id");
        String name          = rs.getString("name");
        String email         = rs.getString("email");
        String password      = rs.getString("password");
        String role          = rs.getString("role");
        String department    = rs.getString("department");
        int yearOfStudy      = rs.getInt("year_of_study");
        String contactNumber = rs.getString("contact_number");
        String employeeId    = rs.getString("employee_id");
        String adminCode     = rs.getString("admin_code");
        boolean active       = rs.getBoolean("active");
        LocalDateTime createdAt = null;
        java.sql.Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            createdAt = ts.toLocalDateTime();
        }

        User user;
        switch (role.toUpperCase()) {
            case "STUDENT": {
                Student s = new Student();
                s.setDepartment(department != null ? department : "");
                s.setYearOfStudy(yearOfStudy >= 1 && yearOfStudy <= 4 ? yearOfStudy : 1);
                s.setContactNumber(contactNumber);
                user = s;
                break;
            }
            case "OWNER": {
                Owner o = new Owner();
                o.setDepartment(department);
                o.setContactNumber(contactNumber);
                o.setEmployeeId(employeeId);
                user = o;
                break;
            }
            case "MANAGER": {
                Manager m = new Manager();
                m.setDepartment(department);
                m.setContactNumber(contactNumber);
                m.setEmployeeId(employeeId);
                user = m;
                break;
            }
            case "ADMIN": {
                Admin a = new Admin();
                a.setAdminCode(adminCode);
                user = a;
                break;
            }
            default:
                throw new SQLException("Unknown user role: " + role);
        }

        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setActive(active);
        user.setCreatedAt(createdAt);

        return user;
    }
}
