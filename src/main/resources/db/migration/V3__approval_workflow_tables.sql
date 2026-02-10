-- ============================================================
-- V3: Multi-level approval workflow
-- Safe for existing data: adds columns and tables only.
-- ============================================================

-- 1. Add reporting_to (self-referencing FK) to employees
IF NOT EXISTS (
    SELECT 1 FROM sys.columns
    WHERE object_id = OBJECT_ID('employees') AND name = 'reporting_to'
)
BEGIN
    ALTER TABLE employees ADD reporting_to BIGINT NULL;
    ALTER TABLE employees
        ADD CONSTRAINT fk_employee_reporting_to
        FOREIGN KEY (reporting_to) REFERENCES employees(id);
END;
GO

-- 2. Approval authorities: configurable per-employee (who can approve what, who is HR)
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'approval_authorities')
BEGIN
    CREATE TABLE approval_authorities (
        id BIGINT IDENTITY(1,1) NOT NULL,
        employee_id BIGINT NOT NULL,
        can_approve_leave BIT NOT NULL DEFAULT 0,
        can_approve_overtime BIT NOT NULL DEFAULT 0,
        is_hr BIT NOT NULL DEFAULT 0,
        version INT NOT NULL DEFAULT 0,
        is_deleted BIT NOT NULL DEFAULT 0,
        created_by VARCHAR(255),
        update_by VARCHAR(255),
        created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        CONSTRAINT pk_approval_authorities PRIMARY KEY (id),
        CONSTRAINT fk_approval_authority_employee
            FOREIGN KEY (employee_id) REFERENCES employees(id),
        CONSTRAINT uq_approval_authorities_employee UNIQUE (employee_id)
    );
    CREATE INDEX idx_approval_authorities_employee ON approval_authorities(employee_id);
    CREATE INDEX idx_approval_authorities_is_hr ON approval_authorities(is_hr) WHERE is_hr = 1;
END;
GO

-- 3. Approval history: audit trail (do not store current approver on request tables)
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'approval_history')
BEGIN
    CREATE TABLE approval_history (
        id BIGINT IDENTITY(1,1) NOT NULL,
        request_type VARCHAR(30) NOT NULL,
        request_id BIGINT NOT NULL,
        approver_id BIGINT NOT NULL,
        approval_level VARCHAR(20) NOT NULL,
        action VARCHAR(20) NOT NULL,
        action_date DATETIME2 NOT NULL DEFAULT GETDATE(),
        remarks VARCHAR(500),
        version INT NOT NULL DEFAULT 0,
        is_deleted BIT NOT NULL DEFAULT 0,
        created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        CONSTRAINT pk_approval_history PRIMARY KEY (id),
        CONSTRAINT fk_approval_history_approver
            FOREIGN KEY (approver_id) REFERENCES employees(id)
    );
    CREATE INDEX idx_approval_history_request ON approval_history(request_type, request_id);
    CREATE INDEX idx_approval_history_approver ON approval_history(approver_id);
END;
GO

-- 4. Migrate existing PENDING to PENDING_SUPERVISOR (safe for existing data)
UPDATE leave_requests SET status = 'PENDING_SUPERVISOR' WHERE status = 'PENDING';
UPDATE overtime_requests SET status = 'PENDING_SUPERVISOR' WHERE status = 'PENDING';
GO

-- 5. Ensure leave_requests and overtime_requests status column supports new values (VARCHAR(20) is enough)
-- Application uses: PENDING_SUPERVISOR, PENDING_HR, APPROVED, REJECTED
