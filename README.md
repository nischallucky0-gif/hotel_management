# Grand Horizon - Hotel Management System Web Application

A full-stack enterprise **Hotel Management System** built from scratch with a Java &amp; Spring Boot backend, MySQL persistent relational storage, and a responsive vanilla HTML5, CSS3, and JavaScript frontend.

---

## Table of Contents
1. [Architecture & Technology Stack](#1-architecture--technology-stack)
2. [Project Structure](#2-project-structure)
3. [Prerequisites & Software Requirements](#3-prerequisites--software-requirements)
4. [Database Configuration (MySQL)](#4-database-configuration-mysql)
5. [Environment Variables Configuration](#5-environment-variables-configuration)
6. [Building & Running the Application](#6-building--running-the-application)
7. [Frontend Access & Development](#7-frontend-access--development)
8. [REST API Documentation & curl Examples](#8-rest-api-documentation--curl-examples)
9. [Automated Testing](#9-automated-testing)
10. [Business Rules Implemented](#10-business-rules-implemented)

---

## 1. Architecture & Technology Stack

- **Frontend**:
  - Semantic HTML5 for clean, accessible structure.
  - Custom CSS3 for modern design system, responsive grid/flex layouts, and print styles.
  - Vanilla JavaScript (ES6+) with Fetch API for asynchronous backend communication.
  - Zero external JavaScript frameworks (no React, Angular, Vue, Node.js).
- **Backend**:
  - **Java 21+** (compatible with installed OpenJDK 27).
  - **Spring Boot 3.3.5** layered architecture.
  - **Spring Web** for RESTful HTTP endpoints.
  - **Spring Data JPA & Hibernate** for object-relational mapping.
  - **Jakarta Bean Validation** for request validation.
- **Database**:
  - **MySQL 8.0+** for production persistent storage.
  - **H2 in-memory DB** (test / dev fallback profile) for isolated unit/integration tests.
- **Build Tool**:
  - **Apache Maven 3.10+**.

---

## 2. Project Structure

```
HOTEL MANAGMENT SYSTEM/
├── backend/
│   ├── pom.xml                               # Maven project dependencies and build configuration
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/hotel/management/
│   │   │   │   ├── HotelManagementApplication.java  # Spring Boot Main Entry Point
│   │   │   │   ├── config/
│   │   │   │   │   ├── WebMvcConfig.java            # CORS & Static resource mappings
│   │   │   │   │   └── DataInitializer.java         # Automatic seed data for development
│   │   │   │   ├── controller/
│   │   │   │   │   ├── RoomController.java          # Room REST endpoints
│   │   │   │   │   ├── GuestController.java         # Guest REST endpoints
│   │   │   │   │   ├── ReservationController.java   # Reservation & Check-in/out endpoints
│   │   │   │   │   ├── BillingController.java       # Folios, charges & payments endpoints
│   │   │   │   │   ├── DashboardController.java     # Operational statistics endpoint
│   │   │   │   │   └── ReportController.java        # Revenue & occupancy reports
│   │   │   │   ├── service/
│   │   │   │   │   ├── RoomService.java             # Room business rules & queries
│   │   │   │   │   ├── GuestService.java            # Guest registration & lookup
│   │   │   │   │   ├── ReservationService.java      # Booking collision & stay logic
│   │   │   │   │   ├── BillingService.java          # Rate calculation, extras & simulated payments
│   │   │   │   │   ├── DashboardService.java        # Live KPI metric calculations
│   │   │   │   │   └── ReportService.java           # Date-range audits & analytics
│   │   │   │   ├── repository/                      # Spring Data JPA Repositories
│   │   │   │   ├── entity/                          # JPA Entities (Room, Guest, Reservation, Bill, etc.)
│   │   │   │   ├── dto/                             # Request and Response transfer objects
│   │   │   │   └── exception/                       # Custom exceptions and GlobalExceptionHandler
│   │   │   └── resources/
│   │   │       ├── application.properties           # Primary MySQL config (with env var overrides)
│   │   │       ├── application-dev.properties       # Standalone H2 dev profile config
│   │   │       └── static/                          # Bundled HTML5/CSS/JS frontend
│   │   └── test/
│   │       ├── java/com/hotel/management/
│   │       │   ├── RoomServiceTest.java             # Room CRUD unit tests
│   │       │   ├── ReservationServiceTest.java      # Booking & billing lifecycle tests
│   │       │   └── DashboardAndReportServiceTest.java # Stats & reporting tests
│   │       └── resources/
│   │           └── application.properties           # Isolated H2 test database profile
├── frontend/
│   ├── index.html                                   # Dashboard view (KPI cards, gauge, quick actions)
│   ├── rooms.html                                   # Room inventory management
│   ├── guests.html                                  # Guest directory & stay history
│   ├── reservations.html                            # Booking engine & stay schedule
│   ├── checkin-checkout.html                        # Front desk arrivals & departures
│   ├── billing.html                                 # Invoices, extras & simulated payment settlements
│   ├── reports.html                                 # Financial statements & occupancy analytics
│   ├── css/
│   │   └── style.css                                # Design system, modals, print styles
│   └── js/
│       ├── api.js                                   # Centralized Fetch helper, toasts, formatters
│       ├── dashboard.js                             # Live dashboard controller
│       ├── rooms.js                                 # Rooms UI controller
│       ├── guests.js                                # Guests UI controller
│       ├── reservations.js                          # Reservations UI controller
│       ├── checkin-checkout.js                      # Front desk reception controller
│       ├── billing.js                               # Billing & invoice printable generator
│       └── reports.js                               # Reporting charts & data controller
├── database/
│   ├── schema.sql                                   # MySQL DDL table schema with indexes & FKs
│   └── sample-data.sql                              # Initial development dataset
├── .gitignore
└── README.md
```

---

## 3. Prerequisites & Software Requirements

Before running the application, ensure the following are installed:
- **macOS** (or Linux/Windows).
- **Java Development Kit (JDK 21 or later)**:
  ```bash
  # Check your installed version:
  java -version
  ```
  *(On macOS with Homebrew: `/opt/homebrew/opt/openjdk/bin/java -version`)*
- **Apache Maven 3.9+**:
  ```bash
  mvn -version
  ```
- **MySQL Server 8.0+** (Optional if using the standalone `dev` profile):
  ```bash
  mysql --version
  ```

---

## 4. Database Configuration (MySQL)

### Step 1: Start MySQL Server
Ensure your MySQL server daemon is running:
```bash
brew services start mysql
```

### Step 2: Initialize Database and Schema
Execute the provided SQL script to create the database and tables:
```bash
mysql -u root -p < database/schema.sql
```

*(Optional)* Seed sample data into MySQL:
```bash
mysql -u root -p < database/sample-data.sql
```

---

## 5. Environment Variables Configuration

The application uses standard Spring Boot externalized configuration. Sensitive credentials are never committed in plaintext.

You can customize your database connection using environment variables:

| Variable | Description | Default Value |
|---|---|---|
| `DB_URL` | MySQL JDBC Connection URL | `jdbc:mysql://localhost:3306/hotel_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` |
| `DB_USERNAME` | MySQL Database Username | `root` |
| `DB_PASSWORD` | MySQL Database Password | *(empty)* |
| `PORT` | Spring Boot Server Port | `8088` |

### Setting variables in Terminal:
```bash
export DB_URL="jdbc:mysql://localhost:3306/hotel_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DB_USERNAME="your_username"
export DB_PASSWORD="your_password"
```

---

## 6. Building & Running the Application

### Option A: Run with Primary MySQL Database (Standard)
1. Open Terminal in the `backend/` directory:
   ```bash
   cd "backend"
   ```
2. Run using Maven:
   ```bash
   mvn spring-boot:run
   ```
   *(Or on macOS with Homebrew: `/opt/homebrew/bin/mvn spring-boot:run`)*

### Option B: Run Standalone Development Profile (Zero Setup H2 Mode)
If you wish to explore the application without configuring a local MySQL server:
```bash
cd "backend"
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
*(The backend will automatically start an in-memory MySQL-compatible database and initialize realistic sample rooms, guests, and reservations!)*

### Option C: Run Pre-Built Executable JAR
```bash
cd "backend"
mvn package
java -jar target/hotel-management-system-1.0.0.jar
```

---

## 7. Frontend Access & Development

Once the Spring Boot backend is running on `http://localhost:8088`:

1. **Direct Web Browser Access (Recommended)**:
   Navigate to:
   ```
   http://localhost:8088/
   ```
   Spring Boot automatically serves the responsive HTML5/CSS3/JavaScript web application directly from the server.

2. **Standalone Frontend Mode**:
   You can also open any file in `frontend/` (e.g. `frontend/index.html`) directly in Google Chrome, Safari, or via VS Code Live Server. The centralized `frontend/js/api.js` automatically connects to `http://localhost:8088` with enabled CORS.

---

## 8. REST API Documentation & curl Examples

All REST API endpoints use standard JSON request/response bodies.

### Rooms
- `GET /api/rooms` - List rooms (supports `query`, `type`, `status` filters).
- `GET /api/rooms/{id}` - Get room details.
- `POST /api/rooms` - Create a new room.
- `PUT /api/rooms/{id}` - Update room details.
- `DELETE /api/rooms/{id}` - Delete a room (blocked if active stay exists).
- `PATCH /api/rooms/{id}/status?status=MAINTENANCE` - Toggle room status.

### Guests
- `GET /api/guests` - Search and list registered guests.
- `GET /api/guests/{id}` - Get guest profile.
- `POST /api/guests` - Register a new guest.
- `PUT /api/guests/{id}` - Update guest details.
- `GET /api/guests/{id}/reservations` - View full stay history of a guest.

### Reservations & Operations
- `GET /api/reservations` - List reservations (supports `query`, `status`, `fromDate`, `toDate`).
- `GET /api/reservations/{id}` - Get reservation details.
- `POST /api/reservations` - Book a room (validates capacity, date conflicts, auto-calculates total).
- `PUT /api/reservations/{id}` - Modify reservation.
- `PATCH /api/reservations/{id}/cancel` - Cancel reservation (frees up room).
- `POST /api/reservations/{id}/check-in` - Check in guest (sets room to `OCCUPIED`).
- `POST /api/reservations/{id}/check-out` - Check out guest (sets room to `AVAILABLE`).

### Billing & Simulated Payments
- `GET /api/bills` - List all folios (supports `status` filter: `UNPAID`, `PARTIALLY_PAID`, `PAID`).
- `GET /api/bills/{id}` - Get invoice details with line items and payments.
- `POST /api/bills/{id}/charges` - Add incidental charge (Food, Laundry, Room Service, etc.).
- `POST /api/bills/{id}/payments` - Record payment settlement (`CASH`, `CARD`, `UPI`).

### Dashboard & Reports
- `GET /api/dashboard/stats` - Live KPIs (available/occupied rooms, arrivals, revenue).
- `GET /api/reports/revenue?fromDate=...&toDate=...` - Financial revenue breakdown.
- `GET /api/reports/occupancy` - Room inventory & occupancy percentages.
- `GET /api/reports/bookings?fromDate=...&toDate=...` - Date-range reservation audit.

---

### Sample curl Requests

#### 1. Create a New Room:
```bash
curl -X POST http://localhost:8080/api/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "roomNumber": "501",
    "type": "DELUXE",
    "pricePerNight": 175.00,
    "maxOccupancy": 2,
    "description": "Panoramic penthouse view with private balcony",
    "status": "AVAILABLE"
  }'
```

#### 2. Create a Reservation:
```bash
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "roomId": 1,
    "guestFullName": "Eleanor Vance",
    "guestEmail": "eleanor.vance@example.com",
    "guestPhoneNumber": "+1-555-8821",
    "checkInDate": "2026-11-10",
    "checkOutDate": "2026-11-14",
    "numberOfGuests": 1,
    "notes": "Late check-in requested"
  }'
```

#### 3. Perform Guest Check-In:
```bash
curl -X POST http://localhost:8080/api/reservations/1/check-in
```

#### 4. Post an Incidental Charge (Food & Beverage):
```bash
curl -X POST http://localhost:8080/api/bills/1/charges \
  -H "Content-Type: application/json" \
  -d '{
    "serviceCategory": "Food & Beverage",
    "description": "Club Sandwich & Espresso",
    "amount": 28.50
  }'
```

#### 5. Record a Simulated Payment (Card):
```bash
curl -X POST http://localhost:8080/api/bills/1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.00,
    "paymentMethod": "CARD",
    "notes": "Front Desk Terminal 1"
  }'
```

---

## 9. Automated Testing

The project includes an automated test suite verifying business rules, date logic, collisions, and calculations:

```bash
cd backend
mvn test
```

### Test Cases Covered:
1. `testCreateAndGetRoom` - Verifies room persistence and field mapping.
2. `testPreventDuplicateRoomNumber` - Enforces unique room numbers.
3. `testUpdateRoom` - Validates room modification.
4. `testSearchAndFilterRooms` - Tests search and status filtering.
5. `testCreateReservation` - Validates automated server-side night calculation and billing creation.
6. `testInvalidDateRanges` - Validates that checkout must be after checkin.
7. `testOverlappingReservations` - Prevents double bookings for the same room.
8. `testRoomCapacityViolations` - Blocks bookings exceeding room max occupancy.
9. `testCheckInAndCheckOutWorkflow` - Verifies state machine transitions (`CONFIRMED` → `CHECKED_IN` → `CHECKED_OUT`) and room occupancy updates (`AVAILABLE` → `OCCUPIED` → `AVAILABLE`).
10. `testBillingAndPayments` - Validates incidental charge accumulation, partial payments, and full settlement.
11. `testDashboardStats` - Verifies live dashboard statistics.
12. `testReports` - Tests financial revenue generation and occupancy reports.

---

## 10. Business Rules Implemented

- **Same-Day Turnover**: The overlap detection allows a guest to check out on date `D` and a new guest to check in on the same date `D`.
- **Backend Price Calculation**: Rates are computed on the backend based on `nights * pricePerNight` and cannot be tampered with by the client.
- **Maintenance Lock**: Rooms marked `MAINTENANCE` cannot be reserved.
- **Folio Balance Tracking**: Payments cannot exceed remaining balance; bill statuses automatically transition (`UNPAID` → `PARTIALLY_PAID` → `PAID`).
- **Simulated Payment Transactions**: Clearly labelled as simulated records for educational compliance without third-party dependencies.
- **Printable Invoices**: Native browser `@media print` layout formatting professional hotel tax invoices.

