-- ============================================================
-- V5: Claim module (expense claims: taxi, phone bill, etc.)
-- Run against your existing HRMS database (SQL Server).
-- ============================================================

-- 1. Add can_approve_claim to approval_authorities
IF NOT EXISTS (
    SELECT 1 FROM sys.columns
    WHERE object_id = OBJECT_ID('approval_authorities') AND name = 'can_approve_claim'
)
BEGIN
    ALTER TABLE approval_authorities ADD can_approve_claim BIT NOT NULL DEFAULT 0;
END;
GO

-- 2. Claim types table
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'claim_types')
BEGIN
    CREATE TABLE claim_types (
        id BIGINT IDENTITY(1,1) NOT NULL,
        code VARCHAR(50) NOT NULL UNIQUE,
        name VARCHAR(100) NOT NULL,
        description VARCHAR(255),
        max_amount_per_claim DECIMAL(19,4),
        requires_receipt BIT NOT NULL DEFAULT 0,
        currency VARCHAR(10) DEFAULT 'USD',
        is_active BIT NOT NULL DEFAULT 1,
        version INT NOT NULL DEFAULT 0,
        is_deleted BIT NOT NULL DEFAULT 0,
        created_by VARCHAR(255),
        update_by VARCHAR(255),
        created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        CONSTRAINT pk_claim_types PRIMARY KEY (id)
    );
    CREATE INDEX idx_claim_types_code ON claim_types(code);
    CREATE INDEX idx_claim_types_active ON claim_types(is_active) WHERE is_active = 1;
END;
GO

-- 2b. Add currency to claim_types if missing (for existing installations)
IF NOT EXISTS (
    SELECT 1 FROM sys.columns
    WHERE object_id = OBJECT_ID('claim_types') AND name = 'currency'
)
BEGIN
    ALTER TABLE claim_types ADD currency VARCHAR(10) DEFAULT 'USD';
END;
GO

-- 3. Claims table
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'claims')
BEGIN
    CREATE TABLE claims (
        id BIGINT IDENTITY(1,1) NOT NULL,
        claim_number VARCHAR(50) NOT NULL UNIQUE,
        employee_id BIGINT NOT NULL,
        claim_type_id BIGINT NOT NULL,
        total_amount DECIMAL(19,4) NOT NULL,
        currency VARCHAR(10) NOT NULL DEFAULT 'USD',
        claim_date DATE NOT NULL,
        description VARCHAR(500),
        status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
        submitted_at DATETIME2,
        approved_by BIGINT,
        approved_at DATETIME2,
        rejection_remarks VARCHAR(500),
        version INT NOT NULL DEFAULT 0,
        is_deleted BIT NOT NULL DEFAULT 0,
        created_by VARCHAR(255),
        update_by VARCHAR(255),
        created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        CONSTRAINT pk_claims PRIMARY KEY (id),
        CONSTRAINT fk_claims_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
        CONSTRAINT fk_claims_claim_type FOREIGN KEY (claim_type_id) REFERENCES claim_types(id),
        CONSTRAINT fk_claims_approved_by FOREIGN KEY (approved_by) REFERENCES employees(id)
    );
    CREATE INDEX idx_claims_employee ON claims(employee_id);
    CREATE INDEX idx_claims_claim_type ON claims(claim_type_id);
    CREATE INDEX idx_claims_status ON claims(status);
    CREATE INDEX idx_claims_claim_date ON claims(claim_date);
END;
GO

-- 4. Claim attachments table
IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = 'claim_attachments')
BEGIN
    CREATE TABLE claim_attachments (
        id BIGINT IDENTITY(1,1) NOT NULL,
        claim_id BIGINT NOT NULL,
        file_name VARCHAR(255) NOT NULL,
        file_path VARCHAR(500) NOT NULL,
        file_type VARCHAR(100),
        file_size BIGINT,
        attachment_type VARCHAR(30) DEFAULT 'RECEIPT',
        version INT NOT NULL DEFAULT 0,
        is_deleted BIT NOT NULL DEFAULT 0,
        created_by VARCHAR(255),
        update_by VARCHAR(255),
        created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        updated_at DATETIME2 NOT NULL DEFAULT GETDATE(),
        CONSTRAINT pk_claim_attachments PRIMARY KEY (id),
        CONSTRAINT fk_claim_attachments_claim FOREIGN KEY (claim_id) REFERENCES claims(id) ON DELETE CASCADE
    );
    CREATE INDEX idx_claim_attachments_claim ON claim_attachments(claim_id);
END;
GO

-- 5. Seed default claim types
IF NOT EXISTS (SELECT 1 FROM claim_types WHERE code = 'TAXI')
BEGIN
    INSERT INTO claim_types (code, name, description, max_amount_per_claim, requires_receipt, currency, is_active, version, is_deleted)
    VALUES ('TAXI', 'Taxi Charges', 'Transportation by taxi/cab', 0, 1, 'USD', 1, 0, 0);
END;
IF NOT EXISTS (SELECT 1 FROM claim_types WHERE code = 'PHONE')
BEGIN
    INSERT INTO claim_types (code, name, description, max_amount_per_claim, requires_receipt, currency, is_active, version, is_deleted)
    VALUES ('PHONE', 'Phone Bill', 'Mobile/landline charges', 0, 1, 'USD', 1, 0, 0);
END;
IF NOT EXISTS (SELECT 1 FROM claim_types WHERE code = 'MEALS')
BEGIN
    INSERT INTO claim_types (code, name, description, max_amount_per_claim, requires_receipt, currency, is_active, version, is_deleted)
    VALUES ('MEALS', 'Meals', 'Business meal expenses', 0, 1, 'USD', 1, 0, 0);
END;
IF NOT EXISTS (SELECT 1 FROM claim_types WHERE code = 'OTHER')
BEGIN
    INSERT INTO claim_types (code, name, description, max_amount_per_claim, requires_receipt, currency, is_active, version, is_deleted)
    VALUES ('OTHER', 'Other Expenses', 'Miscellaneous expenses', 0, 0, 'USD', 1, 0, 0);
END;
GO

-- 6. Grant claim approval to existing supervisors (optional - run if you want existing supervisors to approve claims)
UPDATE approval_authorities SET can_approve_claim = can_approve_leave WHERE can_approve_leave = 1;
GO
