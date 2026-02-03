-- Roles Table
CREATE TABLE roles (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

-- Users Table
CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

-- User Roles Junction Table
CREATE TABLE users_roles (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Departments Table
CREATE TABLE departments (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Employees Table
CREATE TABLE employees (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    join_date DATETIME2 NOT NULL,
    status VARCHAR(20) NOT NULL,
    user_id BIGINT,
    department_id INT,
    position VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- Attendance Table
CREATE TABLE attendance (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    check_in_time DATETIME2,
    check_out_time DATETIME2,
    date DATETIME2 NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

-- Leave Requests Table
CREATE TABLE leave_requests (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    start_date DATETIME2 NOT NULL,
    end_date DATETIME2 NOT NULL,
    reason VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    leave_type VARCHAR(50),
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

-- Overtime Requests Table
CREATE TABLE overtime_requests (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    date DATETIME2 NOT NULL,
    hours DECIMAL(4,2) NOT NULL,
    reason VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

-- Holidays Table
CREATE TABLE holidays (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    date DATETIME2 NOT NULL
);

-- Indexes for performance
CREATE INDEX idx_employee_id ON employees(employee_id);
CREATE INDEX idx_attendance_employee_date ON attendance(employee_id, date);
CREATE INDEX idx_leave_requests_employee_status ON leave_requests(employee_id, status);
CREATE INDEX idx_overtime_requests_employee_status ON overtime_requests(employee_id, status);
