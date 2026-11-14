-- ============================================================
-- SHARESPACE – Campus Resource Sharing System
-- Database Schema and Sample Data Script
-- Compatible with MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS sharespace;
USE sharespace;

-- ------------------------------------------------------------
-- Drop existing tables in reverse dependency order
-- ------------------------------------------------------------
DROP TABLE IF EXISTS damage_reports;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS resources;
DROP TABLE IF EXISTS users;

-- ------------------------------------------------------------
-- 1. Table: users
-- Roles: STUDENT, OWNER, MANAGER, ADMIN
-- ------------------------------------------------------------
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'OWNER', 'MANAGER', 'ADMIN') NOT NULL,
    department VARCHAR(100),
    year_of_study INT CHECK (year_of_study IS NULL OR (year_of_study >= 1 AND year_of_study <= 4)),
    contact_number VARCHAR(20),
    employee_id VARCHAR(50),
    admin_code VARCHAR(50),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- 2. Table: resources
-- Listed by owners; requested by students; issued by managers
-- ------------------------------------------------------------
CREATE TABLE resources (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    category VARCHAR(100) NOT NULL,
    description TEXT,
    total_quantity INT NOT NULL DEFAULT 1 CHECK (total_quantity >= 0),
    available_quantity INT NOT NULL DEFAULT 1 CHECK (available_quantity >= 0),
    location VARCHAR(200),
    owner_id INT NOT NULL,
    owner_name VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_resources_owner FOREIGN KEY (owner_id) 
        REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- 3. Table: bookings
-- Statuses: PENDING, APPROVED, REJECTED, ISSUED, RETURNED, CANCELLED
-- ------------------------------------------------------------
CREATE TABLE bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    student_name VARCHAR(100),
    resource_id INT NOT NULL,
    resource_name VARCHAR(150),
    quantity_requested INT NOT NULL DEFAULT 1 CHECK (quantity_requested > 0),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'ISSUED', 'RETURNED', 'CANCELLED') DEFAULT 'PENDING',
    remarks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_student FOREIGN KEY (student_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_bookings_resource FOREIGN KEY (resource_id) 
        REFERENCES resources(id) ON DELETE CASCADE,
    CONSTRAINT chk_booking_dates CHECK (end_date > start_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Index for checking active booking overlaps efficiently
CREATE INDEX idx_bookings_overlap 
    ON bookings(resource_id, status, start_date, end_date);

-- ------------------------------------------------------------
-- 4. Table: transactions
-- Physical issue and return tracking managed by Managers
-- ------------------------------------------------------------
CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    manager_id INT NOT NULL,
    manager_name VARCHAR(100),
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    returned_at TIMESTAMP NULL,
    condition_on_return VARCHAR(50),
    notes TEXT,
    CONSTRAINT fk_transactions_booking FOREIGN KEY (booking_id) 
        REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_transactions_manager FOREIGN KEY (manager_id) 
        REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- 5. Table: damage_reports
-- Logged by Managers when returned resources are damaged
-- ------------------------------------------------------------
CREATE TABLE damage_reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id INT NOT NULL,
    resource_id INT NOT NULL,
    resource_name VARCHAR(150),
    damage_description TEXT NOT NULL,
    severity ENUM('MINOR', 'MODERATE', 'SEVERE') NOT NULL,
    reported_by_manager_id INT NOT NULL,
    reported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_damage_transaction FOREIGN KEY (transaction_id) 
        REFERENCES transactions(id) ON DELETE CASCADE,
    CONSTRAINT fk_damage_resource FOREIGN KEY (resource_id) 
        REFERENCES resources(id) ON DELETE CASCADE,
    CONSTRAINT fk_damage_manager FOREIGN KEY (reported_by_manager_id) 
        REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- SAMPLE DATASET
-- Users: 2 Students, 2 Owners, 1 Manager, 1 Admin
-- Resources: 6 Resources across multiple categories
-- Bookings: Sample requests with varied statuses
-- Transactions & Damage Report
-- ============================================================

-- 1. Insert Users
INSERT INTO users (id, name, email, password, role, department, year_of_study, contact_number, employee_id, admin_code, active)
VALUES 
    -- Students
    (1, 'Alice Johnson', 'alice@campus.edu', 'password123', 'STUDENT', 'Computer Science', 3, NULL, NULL, NULL, TRUE),
    (2, 'Bob Smith', 'bob@campus.edu', 'password123', 'STUDENT', 'Mechanical Engineering', 1, NULL, NULL, NULL, TRUE),
    -- Owners
    (3, 'Dr. Alan Turing', 'turing@campus.edu', 'password123', 'OWNER', 'Computer Science Dept', NULL, '9876543210', NULL, NULL, TRUE),
    (4, 'Prof. Marie Curie', 'curie@campus.edu', 'password123', 'OWNER', 'Physics & Chemistry Lab', NULL, '9876543211', NULL, NULL, TRUE),
    -- Manager
    (5, 'Marcus Vance', 'manager@campus.edu', 'password123', 'MANAGER', NULL, NULL, NULL, 'MGR-2024-01', NULL, TRUE),
    -- Admin
    (6, 'System Administrator', 'admin@sharespace.edu', 'admin123', 'ADMIN', NULL, NULL, NULL, NULL, 'ADM-SEC-999', TRUE);

-- Reset auto-increment
ALTER TABLE users AUTO_INCREMENT = 7;

-- 2. Insert Resources (At least 5 resources across various owners and categories)
INSERT INTO resources (id, name, category, description, total_quantity, available_quantity, location, owner_id, owner_name, active)
VALUES
    (1, 'Dell XPS 15 Laptop', 'LAPTOP', 'Intel i7 16GB RAM for high-performance computing tasks', 4, 3, 'CS Dept Lab Room 301', 3, 'Dr. Alan Turing', TRUE),
    (2, 'Sony Alpha A7 III Camera', 'CAMERA', 'Full frame mirrorless 4K camera with 28-70mm lens', 2, 2, 'Media Center Studio A', 3, 'Dr. Alan Turing', TRUE),
    (3, 'Epson 4K Laser Projector', 'PROJECTOR', '4000 lumens high-contrast conference projector', 3, 2, 'AV Room 102', 3, 'Dr. Alan Turing', TRUE),
    (4, 'Digital Storage Oscilloscope', 'LAB_EQUIPMENT', '100MHz 2-channel oscilloscope with probes (Year 2+ only)', 5, 5, 'Electronics Lab E-204', 4, 'Prof. Marie Curie', TRUE),
    (5, 'Spectrophotometer UV-Vis', 'SPECIAL_EQUIPMENT', 'Double beam spectrophotometer for chemical analysis (Year 3+ only)', 2, 2, 'Advanced Chemistry Lab C-105', 4, 'Prof. Marie Curie', TRUE),
    (6, 'Conference Seminar Hall A', 'SEMINAR_ROOM', 'Capacity 80 with smart podium and microphone system', 1, 1, 'Main Academic Block 2nd Floor', 3, 'Dr. Alan Turing', TRUE);

ALTER TABLE resources AUTO_INCREMENT = 7;

-- 3. Insert Bookings (Various statuses: PENDING, APPROVED, ISSUED, RETURNED)
INSERT INTO bookings (id, student_id, student_name, resource_id, resource_name, quantity_requested, start_date, end_date, status, remarks, created_at)
VALUES
    -- Pending booking from Alice
    (1, 1, 'Alice Johnson', 1, 'Dell XPS 15 Laptop', 1, '2026-11-20', '2026-11-25', 'PENDING', 'Requested for final year machine learning project', '2026-11-14 09:00:00'),
    -- Approved booking (waiting for manager issue)
    (2, 1, 'Alice Johnson', 3, 'Epson 4K Laser Projector', 1, '2026-11-18', '2026-11-19', 'APPROVED', 'Approved by owner Dr. Alan Turing', '2026-11-14 09:15:00'),
    -- Issued booking (currently with student Bob)
    (3, 2, 'Bob Smith', 1, 'Dell XPS 15 Laptop', 1, '2026-11-14', '2026-11-16', 'ISSUED', 'Resource issued by manager: Marcus Vance', '2026-11-13 14:00:00'),
    -- Past returned booking
    (4, 1, 'Alice Johnson', 2, 'Sony Alpha A7 III Camera', 1, '2026-11-01', '2026-11-05', 'RETURNED', 'Returned to manager in good condition', '2026-11-01 10:00:00');

ALTER TABLE bookings AUTO_INCREMENT = 5;

-- 4. Insert Transactions (For issued and returned bookings)
INSERT INTO transactions (id, booking_id, manager_id, manager_name, issued_at, returned_at, condition_on_return, notes)
VALUES
    -- Active issued transaction
    (1, 3, 5, 'Marcus Vance', '2026-11-14 10:00:00', NULL, NULL, 'Handed over charger and laptop bag'),
    -- Completed returned transaction
    (2, 4, 5, 'Marcus Vance', '2026-11-01 11:00:00', '2026-11-05 16:30:00', 'GOOD', 'Returned on time, all accessories intact');

ALTER TABLE transactions AUTO_INCREMENT = 3;

-- 5. Insert Damage Report Sample (historical record)
INSERT INTO damage_reports (id, transaction_id, resource_id, resource_name, damage_description, severity, reported_by_manager_id, reported_at)
VALUES
    (1, 2, 2, 'Sony Alpha A7 III Camera', 'Minor cosmetic scratch on lens hood rim, optics clean', 'MINOR', 5, '2026-11-05 16:45:00');

ALTER TABLE damage_reports AUTO_INCREMENT = 2;

-- Verification query
SELECT 'Database setup completed successfully!' AS status;
