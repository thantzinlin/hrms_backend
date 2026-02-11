-- ============================================================
-- V4: Leave Type - configurable leave types
-- ============================================================

-- 1. Create leave_type table
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'leave_type')
BEGIN
    CREATE TABLE leave_type (
        id BIGINT IDENTITY(1,1) NOT NULL,
        code VARCHAR(50) NOT NULL,
        name VARCHAR(100) NOT NULL,
        description VARCHAR(255),
        max_days INT,
        is_active BIT NOT NULL DEFAULT 1,
        version INT NOT NULL DEFAULT 0,
        is_deleted BIT NOT NULL DEFAULT 0,
        created_by VARCHAR(255),
        update_by VARCHAR(255),
        created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        CONSTRAINT pk_leave_type PRIMARY KEY (id),
        CONSTRAINT uq_leave_type_code UNIQUE (code)
    );
END;
GO

-- 2. Seed default leave types (if empty)
IF NOT EXISTS (SELECT 1 FROM leave_type)
BEGIN
    INSERT INTO leave_type (code, name, description, max_days, is_active, version, is_deleted, created_at, updated_at)
    VALUES
        ('ANNUAL', 'Annual Leave', 'Paid annual leave', 30, 1, 0, 0, GETDATE(), GETDATE()),
        ('SICK', 'Sick Leave', 'Medical/sick leave', 15, 1, 0, 0, GETDATE(), GETDATE()),
        ('CASUAL', 'Casual Leave', 'Casual/personal leave', 12, 1, 0, 0, GETDATE(), GETDATE()),
        ('MATERNITY', 'Maternity Leave', 'Maternity leave', 90, 1, 0, 0, GETDATE(), GETDATE()),
        ('PATERNITY', 'Paternity Leave', 'Paternity leave', 14, 1, 0, 0, GETDATE(), GETDATE()),
        ('UNPAID', 'Unpaid Leave', 'Unpaid leave', NULL, 1, 0, 0, GETDATE(), GETDATE());
END;
GO

-- 3. Add leave_type_id to leave_requests
IF NOT EXISTS (
    SELECT 1 FROM sys.columns
    WHERE object_id = OBJECT_ID('leave_requests') AND name = 'leave_type_id'
)
BEGIN
    ALTER TABLE leave_requests ADD leave_type_id BIGINT NULL;

    -- 4. Migrate existing leave_type string to leave_type_id
    UPDATE lr
    SET lr.leave_type_id = lt.id
    FROM leave_requests lr
    INNER JOIN leave_type lt ON lt.code = lr.leave_type
    WHERE lr.leave_type IS NOT NULL;

    -- 5. For any unmigrated rows, assign ANNUAL as default
    UPDATE lr
    SET lr.leave_type_id = (SELECT TOP 1 id FROM leave_type WHERE code = 'ANNUAL')
    FROM leave_requests lr
    WHERE lr.leave_type_id IS NULL;

    -- 6. Make leave_type_id NOT NULL and add FK
    ALTER TABLE leave_requests ALTER COLUMN leave_type_id BIGINT NOT NULL;
    ALTER TABLE leave_requests
        ADD CONSTRAINT fk_leave_request_leave_type
        FOREIGN KEY (leave_type_id) REFERENCES leave_type(id);

    -- 7. Drop legacy leave_type column
    ALTER TABLE leave_requests DROP COLUMN leave_type;
END;
GO
