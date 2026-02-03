# HRMS (Human Resource Management System) Backend

This is a complete backend-only HRMS built using Java Spring Boot and Microsoft SQL Server.

## Tech Stack

- **Java**: 17
- **Spring Boot**: 3.2.4 (latest as of project creation)
- **Spring Security**: For authentication and authorization
- **JWT Authentication**: Access + Refresh tokens
- **JPA / Hibernate**: For data persistence
- **Microsoft SQL Server**: Database
- **Maven**: Build automation tool
- **Lombok**: To reduce boilerplate code
- **Swagger / OpenAPI**: For API documentation

## Features

1.  **Authentication & Authorization**
    - Login API (`/api/auth/signin`)
    - JWT-based authentication (`/api/auth/refreshtoken`)
    - BCrypt password hashing
    - Role-based access control (ADMIN, HR, MANAGER, EMPLOYEE)
    - JWT filter and security configuration

2.  **Employee Management**
    - CRUD APIs for employees (`/api/employees`)
    - Employee profile linked to user account
    - Fields: `employeeId`, `name`, `email`, `phone`, `department`, `position`, `joinDate`, `status`

3.  **Attendance Management**
    - Daily check-in / check-out APIs (`/api/attendance/check-in`, `/api/attendance/check-out`)
    - Prevent duplicate check-in per day
    - Calculate working hours
    - Attendance report APIs (by employee, date range) (`/api/attendance/report/employee/{employeeId}`, `/api/attendance/report/date/{date}`)

4.  **Overtime (OT) Management**
    - OT request submission (`/api/overtime`)
    - Approval / rejection workflow (`/api/overtime/{id}/status`)
    - OT status: PENDING, APPROVED, REJECTED
    - Retrieve OT requests by employee or pending status (`/api/overtime/employee/{employeeId}`, `/api/overtime/pending`)

5.  **Leave Management**
    - Leave request submission (`/api/leaves`)
    - Approval / rejection workflow (`/api/leaves/{id}/status`)
    - Leave balance calculation (`/api/leaves/{id}/calculate-days`)
    - Leave history (`/api/leaves/employee/{employeeId}`, `/api/leaves/pending`)

6.  **Admin Management**
    - Department CRUD (`/api/admin/departments`)
    - Holiday management (`/api/admin/holidays`)
    - Role management (Implicitly handled through employee creation and security configuration)

7.  **Dashboard APIs**
    - Total employees (`/api/dashboard/stats`)
    - Today attendance count
    - Pending leave count
    - Pending OT count

## Architecture

- **Layered Architecture**: Controller → Service → Repository layers
- **DTOs**: For request/response payloads
- **Global Exception Handling**: To provide consistent error responses
- **Pagination and Sorting**: Implemented for employee listing

## Database (MSSQL)

- `IDENTITY` primary keys
- `DATETIME2` for dates
- Foreign keys and indexes defined in `src/main/resources/schema.sql`

## Setup Instructions

### Prerequisites

- Java 17 Development Kit (JDK)
- Apache Maven
- Microsoft SQL Server (with a database named `hrms` created)

### 1. Clone the Repository

```bash
git clone <repository-url>
cd hrms
```

### 2. Configure Database

Update the `src/main/resources/application.properties` file with your Microsoft SQL Server database credentials:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=hrms;encrypt=true;trustServerCertificate=true
spring.datasource.username=your_username
spring.datasource.password=your_password
```

**Note:** Ensure `encrypt=true;trustServerCertificate=true` is present if you are connecting to a local SQL Server instance or a server with a self-signed certificate. For production, consider using a proper SSL/TLS setup.

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080` (or the port configured in `application.properties`).

### 5. Access Swagger UI (API Documentation)

Once the application is running, you can access the API documentation via Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

## Initial Data

On application startup, the following data will be seeded:

- **Roles**: ADMIN, HR, MANAGER, EMPLOYEE
- **Admin User**:
  - **Username**: `admin`
  - **Email**: `admin@hrms.com`
  - **Password**: `password` (Please change this after the first login)

## API Endpoints Summary

Below is a summary of the main API endpoints. Refer to the Swagger UI for detailed request/response schemas and examples.

### Authentication

- `POST /api/auth/signin`: User login, returns JWT tokens.
- `POST /api/auth/refreshtoken`: Get a new access token using a refresh token.

### Employee Management

- `POST /api/employees`: Create a new employee (ADMIN, HR).
- `GET /api/employees`: Get all employees (ADMIN, HR, MANAGER). Supports pagination.
- `GET /api/employees/{id}`: Get employee by ID (ADMIN, HR, MANAGER).
- `PUT /api/employees/{id}`: Update employee details (ADMIN, HR).
- `DELETE /api/employees/{id}`: Delete an employee (ADMIN, HR).

### Attendance Management

- `POST /api/attendance/check-in`: Record employee check-in (EMPLOYEE, MANAGER, HR, ADMIN).
- `POST /api/attendance/check-out`: Record employee check-out (EMPLOYEE, MANAGER, HR, ADMIN).
- `GET /api/attendance/report/employee/{employeeId}?startDate=...&endDate=...`: Get attendance report for an employee (ADMIN, HR, MANAGER).
- `GET /api/attendance/report/date/{date}`: Get attendance report for a specific date (ADMIN, HR, MANAGER).

### Overtime Management

- `POST /api/overtime`: Submit an overtime request (EMPLOYEE, MANAGER, HR, ADMIN).
- `GET /api/overtime`: Get all overtime requests (ADMIN, HR, MANAGER).
- `GET /api/overtime/{id}`: Get overtime request by ID (ADMIN, HR, MANAGER, EMPLOYEE).
- `PUT /api/overtime/{id}/status`: Update overtime request status (ADMIN, HR, MANAGER).
- `GET /api/overtime/employee/{employeeId}`: Get all overtime requests for a specific employee (ADMIN, HR, MANAGER, EMPLOYEE).
- `GET /api/overtime/pending`: Get all pending overtime requests (ADMIN, HR, MANAGER).

### Leave Management

- `POST /api/leaves`: Submit a leave request (EMPLOYEE, MANAGER, HR, ADMIN).
- `GET /api/leaves`: Get all leave requests (ADMIN, HR, MANAGER).
- `GET /api/leaves/{id}`: Get leave request by ID (ADMIN, HR, MANAGER, EMPLOYEE).
- `PUT /api/leaves/{id}/status`: Update leave request status (ADMIN, HR, MANAGER).
- `GET /api/leaves/employee/{employeeId}`: Get all leave requests for a specific employee (ADMIN, HR, MANAGER, EMPLOYEE).
- `GET /api/leaves/pending`: Get all pending leave requests (ADMIN, HR, MANAGER).
- `GET /api/leaves/{id}/calculate-days`: Calculate leave days for a specific leave request (ADMIN, HR, MANAGER, EMPLOYEE).

### Admin Management

- **Departments**
  - `POST /api/admin/departments`: Create a new department (ADMIN).
  - `GET /api/admin/departments`: Get all departments (ADMIN).
  - `GET /api/admin/departments/{id}`: Get department by ID (ADMIN).
  - `PUT /api/admin/departments/{id}`: Update department details (ADMIN).
  - `DELETE /api/admin/departments/{id}`: Delete a department (ADMIN).
- **Holidays**
  - `POST /api/admin/holidays`: Create a new holiday (ADMIN).
  - `GET /api/admin/holidays`: Get all holidays (ADMIN).
  - `GET /api/admin/holidays/{id}`: Get holiday by ID (ADMIN).
  - `PUT /api/admin/holidays/{id}`: Update holiday details (ADMIN).
  - `DELETE /api/admin/holidays/{id}`: Delete a holiday (ADMIN).

### Dashboard

- `GET /api/dashboard/stats`: Get various dashboard statistics (ADMIN, HR, MANAGER).

---

This README provides a comprehensive guide to setting up and using the HRMS backend.
