# ShareSpace – Campus Resource Sharing System
**A Console-Based Java Application with MySQL Persistence**

---

## 1. Project Overview

**ShareSpace** is a complete, modular, console-based resource-sharing management system designed for campus environments (colleges and universities). It allows students and faculty to borrow under-utilized campus resources such as laptops, cameras, projectors, lab equipment, and seminar halls, while providing resource owners, physical managers, and campus administrators dedicated workflows to oversee requests, prevent overbooking, track handovers, and audit damage reports.

### Key Highlights
- **Strictly Console-Based:** Pure terminal interface; zero web frameworks, HTML/CSS/JS, or REST APIs.
- **Role-Based Architecture:** Dedicated dashboards for `STUDENT`, `OWNER`, `MANAGER`, and `ADMIN`.
- **Relational Persistence:** Full MySQL database persistence using JDBC with transaction rollback (`commit`/`rollback`).
- **Academic Milestones:** Covers all core Java syllabus concepts: OOP, Strings, Custom Exceptions, Collections Framework, Multithreading, Concurrency Synchronization, Producer-Consumer, Executor Framework, and JDBC.

---

## 2. Technology Stack & Requirements

| Component | Specification |
|---|---|
| **Language** | Java 21 LTS (supports JDK 17 to JDK 26+) |
| **Database** | MySQL 8.0+ / 8.4 LTS / 9.x |
| **Connectivity** | JDBC (MySQL Connector/J 9.1.0) |
| **Build Tool** | Apache Maven 3.9+ (or direct javac) |
| **Interface** | Standard System Console (Terminal/PowerShell/Command Prompt) |

---

## 3. Project Directory Structure

```
ShareSpace/
│
├── pom.xml                                  <-- Maven project configuration
├── README.md                                <-- Documentation & execution guide
├── database/
│   └── sharespace.sql                       <-- Database schema and seed data
│
└── src/main/java/
    │
    ├── Main.java                            <-- Application entry point
    │
    ├── model/                               <-- Milestone 1: Domain Entities
    │   ├── User.java                        <-- Abstract base user
    │   ├── Student.java                     <-- Borrower subclass
    │   ├── Owner.java                       <-- Provider subclass
    │   ├── Manager.java                     <-- Issue/Return handler
    │   ├── Admin.java                       <-- System administrator
    │   ├── Resource.java                    <-- Implements Comparable<Resource>
    │   ├── Booking.java                     <-- Booking entity with StringBuilder receipt
    │   ├── Transaction.java                 <-- Physical issue/return record
    │   └── DamageReport.java                <-- Condition & severity record
    │
    ├── interfaces/
    │   └── Eligible.java                    <-- Milestone 1: Eligibility check contract
    │
    ├── exceptions/                          <-- Milestone 1: Custom Exception Hierarchy
    │   ├── InvalidUserException.java
    │   ├── InvalidResourceException.java
    │   ├── DuplicateRecordException.java
    │   └── BookingException.java
    │
    ├── collections/                         <-- Milestone 2: Collections Framework
    │   └── ResourceCollectionManager.java  <-- ArrayList, HashMap, HashSet, Iterators, Comparators
    │
    ├── concurrency/                         <-- Milestones 3 & 4: Concurrency & Synchronization
    │   ├── SlotManager.java                 <-- Synchronized critical section slot allocator
    │   ├── BookingTask.java                 <-- Runnable task (start vs run demonstration)
    │   ├── BookingQueue.java                <-- Bounded wait()/notifyAll() request queue
    │   ├── BookingProducer.java             <-- Producer thread adding booking requests
    │   ├── BookingConsumer.java             <-- Consumer thread processing booking requests
    │   └── BookingExecutor.java             <-- ExecutorService, Callable, Future, Simulation
    │
    ├── database/                            <-- Milestone 5: JDBC Connection Manager
    │   └── DBConnection.java                <-- Singleton JDBC DriverManager connection
    │
    ├── dao/                                 <-- Milestone 5: Data Access Objects
    │   ├── UserDAO.java                     <-- User CRUD & role-specific mapping
    │   ├── ResourceDAO.java                 <-- Resource CRUD & queries
    │   ├── BookingDAO.java                  <-- Booking CRUD & overlap checks
    │   ├── TransactionDAO.java              <-- Issue/return transaction operations
    │   └── DamageReportDAO.java             <-- Damage reporting operations
    │
    ├── service/                             <-- Business Services & Transactions
    │   ├── AuthService.java                 <-- Registration & authentication
    │   ├── ResourceService.java             <-- Resource management operations
    │   ├── BookingService.java              <-- ACID multi-step booking transactions
    │   ├── EligibilityService.java          <-- Implements Eligible interface
    │   └── ReportService.java               <-- System audits & StringBuilder reports
    │
    └── menu/                                <-- Terminal User Interfaces
        ├── MainMenu.java                    <-- Main entry (Register, Login, Concurrency Demo, Exit)
        ├── StudentMenu.java                 <-- Student dashboard (11 options)
        ├── OwnerMenu.java                   <-- Owner dashboard (10 options)
        ├── ManagerMenu.java                 <-- Manager dashboard (9 options)
        └── AdminMenu.java                   <-- Admin dashboard (9 options)
```

---

## 4. Database Setup & Configuration

### Step 1: Start MySQL
Ensure your local MySQL service is running on `localhost:3306`.

### Step 2: Initialize Database and Tables
Open your terminal or MySQL Workbench and run:
```bash
mysql -u root -p < database/sharespace.sql
```
*(Enter your MySQL root password when prompted)*

### Step 3: Configure Database Credentials
If your MySQL credentials differ from `root` / `root`, edit:
`src/main/java/com/sharespace/database/DBConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/sharespace?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String USER = "your_mysql_username";
private static final String PASSWORD = "your_mysql_password";
```

---

## 5. Build and Execution Instructions

### Option A: Using Maven (Recommended)
1. Open terminal inside the project directory:
   ```bash
   cd "ShareSpace"
   ```
2. Build the fat JAR with dependencies:
   ```bash
   mvn clean package
   ```
3. Run the application:
   ```bash
   java -jar target/ShareSpace-1.0.0-jar-with-dependencies.jar
   ```

### Option B: Direct Compilation with `javac` and `java`
If running without Maven:
```powershell
# Compile
javac -cp "lib/mysql-connector-j-9.1.0.jar;." -d bin (Get-ChildItem -Recurse src/main/java -Filter *.java | ForEach-Object {$_.FullName})

# Run
java -cp "bin;lib/mysql-connector-j-9.1.0.jar" Main
```

---

## 6. Pre-Configured Sample Accounts

The database seed script (`sharespace.sql`) pre-loads testing accounts:

| Role | Name | Email | Password | Details |
|---|---|---|---|---|
| **STUDENT** | Alice Johnson | `alice@campus.edu` | `password123` | Year 3, Computer Science |
| **STUDENT** | Bob Smith | `bob@campus.edu` | `password123` | Year 1, Mechanical Engg |
| **OWNER** | Dr. Alan Turing | `turing@campus.edu` | `password123` | CS Dept (Laptops, Projectors, Seminar Hall) |
| **OWNER** | Prof. Marie Curie | `curie@campus.edu` | `password123` | Lab Dept (Oscilloscope, Spectrophotometer) |
| **MANAGER** | Marcus Vance | `manager@campus.edu` | `password123` | Resource Custodian (Emp ID: `MGR-2024-01`) |
| **ADMIN** | System Administrator | `admin@sharespace.edu` | `admin123` | Root Admin (Code: `ADM-SEC-999`) |

---

## 7. Milestone-Wise Concept Mapping

### Milestone 1: OOP, Strings, and Exception Handling
- **Encapsulation & Validation:** Private fields across `User`, `Resource`, `Booking`, `Transaction`, `DamageReport`. Validation guards against negative quantities, blank names, and invalid date ranges.
- **Inheritance & Polymorphism:** Abstract base class `User` extended by `Student`, `Owner`, `Manager`, and `Admin`. Each overrides abstract methods `showDashboard()` and `getRoleDescription()`.
- **Interface Implementation:** `Eligible` interface implemented by `EligibilityService` (`isEligible(Student, Resource)`). Enforces year requirements (e.g. `LAB_EQUIPMENT` requires Year ≥ 2; `SPECIAL_EQUIPMENT` requires Year ≥ 3).
- **String Manipulation:** `User.normalizeEmail()` (trim and lowercase), `User.validateEmail()` (validates `@` and domain dot), `User.normalizeName()` (title-casing each word).
- **StringBuilder:** `Booking.generateBookingReceipt()`, `User.generateProfileReport()`, `Resource.generateResourceReport()`, and `ReportService.generateSystemSummary()`.
- **Wrapper Classes:** `Integer.parseInt()`, `Student.parseYear(String s)` returning `Integer` wrapper.
- **Custom Exceptions & Boundaries:** `InvalidUserException`, `InvalidResourceException`, `DuplicateRecordException`, `BookingException` with `throw`, `throws`, and handled at menu boundaries.

### Milestone 2: Collections Framework & Record Management
Implemented in `com.sharespace.collections.ResourceCollectionManager`:
- **`ArrayList<Resource>`:** In-memory indexed collection for ordering and list traversal.
- **`HashMap<Integer, Resource>`:** $O(1)$ fast lookup of resources by their primary key `id`.
- **`HashSet<String>`:** Unique set of normalized resource names to prevent duplicate listings.
- **`Iterator`:** Safe traversal for filtering available resources.
- **`ListIterator`:** Safe in-place updates during resource modifications.
- **`Comparator<Resource>`:** Static comparators `BY_NAME`, `BY_CATEGORY`, `BY_AVAILABILITY`.
- **`Comparable<Resource>`:** Natural alphabetical sorting by resource name implemented on `Resource`.
- **Generics:** All collections and methods use strong typing without raw types.

### Milestone 3: Multithreading and Synchronization
Implemented in `com.sharespace.concurrency.SlotManager`, `BookingTask`, `BookingExecutor`:
- **Problem Scenario:** 5 concurrent student threads simultaneously attempt to reserve 2 available slots for `"Canon DSLR Camera"`.
- **`Runnable` Interface:** `BookingTask` encapsulates concurrent student reservation logic.
- **Thread Management:** `new Thread(task, "StudentThread-N")`, `thread.start()` to spawn OS threads, and `thread.join()` to synchronize main execution.
- **`Thread.start()` vs `Thread.run()`:** Explicitly documented. `start()` creates a new concurrent thread scheduled by the JVM; `run()` merely executes synchronously on the caller's thread.
- **Critical Section:** `SlotManager.reserve(studentName)` is `synchronized`, protecting the check-and-decrement operation so available slots never go below zero.
- **`Thread.sleep()`:** Simulates network and processing latency.
- **`Thread.interrupt()`:** Targeted thread interruption demonstrated on thread 5.

### Milestone 4: Producer-Consumer & Executor Framework
Implemented in `com.sharespace.concurrency.BookingQueue`, `BookingProducer`, `BookingConsumer`:
- **Architecture:** `Producer` adds pending booking IDs $\rightarrow$ bounded `BookingQueue` $\rightarrow$ `Consumer` processes requests and increments atomic counters.
- **Low-Level Coordination:** `wait()` when the queue is empty; `notifyAll()` when work is queued or completed.
- **Spurious Wakeup Protection:** `while (condition)` guards every `wait()` call.
- **Monitor Ownership:** All `wait()`/`notifyAll()` invocations occur within `synchronized` blocks.
- **`ExecutorService`:** Fixed thread pool (`Executors.newFixedThreadPool(3)`) processing asynchronous notifications.
- **`Callable<T>` and `Future<T>`:** `Callable<Integer>` computes total processed bookings; retrieved synchronously via `future.get()`.
- **Clean Shutdown:** Managed via `executor.shutdown()` and `executor.awaitTermination(10, TimeUnit.SECONDS)`.

### Milestone 5: JDBC and MySQL Relational Persistence
- **Singleton Connection:** `DBConnection.getConnection()` manages a single reusable JDBC connection with auto-reconnect.
- **Prepared Statements:** All SQL queries use `PreparedStatement` with parameterized inputs to guard against SQL injection.
- **Result Mapping:** `ResultSet` records mapped directly to Java domain model hierarchies.
- **Resource Management:** `try-with-resources` ensures `Connection`, `PreparedStatement`, and `ResultSet` are closed properly.
- **ACID Database Transactions:**
  - *Booking Approval:* Decrements `resources.available_quantity` AND updates `bookings.status = 'APPROVED'` within a single transaction using `conn.setAutoCommit(false)`, `conn.commit()`, and `conn.rollback()`.
  - *Resource Return:* Updates `transactions.returned_at`, updates `bookings.status = 'RETURNED'`, and restores `resources.available_quantity` atomically.

---

## 8. Role-Based Workflows & Menus

### A. Student Workflow
1. View Available Resources (live catalog)
2. Search Resources by keyword
3. Filter Resources by Category
4. View Detailed Resource Specifications
5. Request Resource Booking (enforces start < end date, checks eligibility, verifies availability)
6. View My Booking Requests
7. Check Specific Booking Status
8. Cancel Eligible Booking (only `PENDING` bookings)
9. View Booking History
10. View My Profile (via `StringBuilder`)
11. Logout

### B. Owner Workflow
1. Add Resource (name, category, quantity, location)
2. View My Listed Resources
3. Update Resource Details
4. Remove/Deactivate Resource
5. View Incoming Booking Requests (pending approval)
6. Approve Booking Request (triggers atomic database reservation)
7. Reject Booking Request (records reason)
8. View Resource Booking History
9. View My Profile
10. Logout

### C. Manager Workflow
1. View Approved Bookings (ready for physical handover)
2. Verify Booking and Borrower identity
3. Issue Resource (creates `Transaction` and marks status `ISSUED`)
4. Record Resource Return (records timestamp, condition, notes, restores quantity)
5. View Currently Issued Resources (`returned_at IS NULL`)
6. Check Resource Condition & in-use quantities
7. Report Damaged Resource (logs `DamageReport` with severity `MINOR`/`MODERATE`/`SEVERE`)
8. View Full Transaction History
9. Logout

### D. Admin Workflow
1. View All Users
2. View Users by Role (`STUDENT`, `OWNER`, `MANAGER`, `ADMIN`)
3. Add, Update, or Deactivate User
4. View All Resources across campus
5. View All Bookings
6. View All Transactions
7. View Damage Reports
8. View System Summary (aggregated statistics report)
9. Logout

---

## 9. Comprehensive Testing Guide

### Test 1: Concurrency Simulation (Milestone 3 & 4)
From the Main Menu, choose option `3`:
```
============================================================
  MILESTONE 3: CONCURRENT BOOKING SIMULATION
============================================================
  Resource: 'Canon DSLR Camera'
  Available Slots: 2
  Students Attempting: 5
  Expected: 2 succeed, 3 fail (no overbooking)
============================================================
[Thread: StudentThread-1] Alice Johnson is attempting to reserve 'Canon DSLR Camera' ...
[Thread: StudentThread-2] Bob Smith is attempting to reserve 'Canon DSLR Camera' ...
[Thread: StudentThread-1] ✔ SUCCESS  — Alice Johnson reserved a slot. Remaining: 1/2
[Thread: StudentThread-2] ✔ SUCCESS  — Bob Smith reserved a slot. Remaining: 0/2
[Thread: StudentThread-3] Carol Davis is attempting to reserve 'Canon DSLR Camera' ...
[Thread: StudentThread-3] ✘ FAILED   — Carol Davis could not reserve. No slots available.
...
============================================================
  SIMULATION RESULTS
============================================================
  Successful Reservations : 2
  Failed Reservations     : 3
  Remaining Slots         : 0/2
  Overbooking occurred    : NO (Correct)
============================================================
```

### Test 2: Invalid Registration (Milestone 1)
- Attempt email without `@`: `InvalidUserException: Invalid email format.`
- Attempt short password (< 6 chars): `InvalidUserException: Password must be at least 6 characters.`
- Attempt duplicate email (`alice@campus.edu`): `DuplicateRecordException: Email already registered.`

### Test 3: Student Eligibility Checking (Milestone 1)
- Login as `bob@campus.edu` (Year 1 student).
- Attempt to book Resource `5` (`Spectrophotometer UV-Vis`):
  `[Eligibility] Special equipment is only available to 3rd year students and above. You are in year 1.`
  `[!] You are not eligible to book this resource.`

### Test 4: End-to-End Booking Lifecycle
1. **Student Request:** Alice requests `Dell XPS 15 Laptop` from `2026-11-20` to `2026-11-25`. Booking is created with status `PENDING`.
2. **Owner Approval:** Dr. Alan Turing logs in, views incoming requests, and approves Booking. Resource available count decreases by 1 in the database transaction.
3. **Manager Issue:** Marcus Vance logs in, views approved bookings, and issues the laptop. Transaction is created, status becomes `ISSUED`.
4. **Manager Return:** When returned, Marcus records the return. Resource availability is safely restored.
