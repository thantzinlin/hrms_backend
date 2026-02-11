-- ============================================================
-- V7: Add personal information columns to employees table
-- Run against your existing HRMS database (SQL Server).
-- ============================================================

-- Father Name
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('employees') AND name = 'father_name')
BEGIN
    ALTER TABLE employees ADD father_name VARCHAR(255) NULL;
END;
GO

-- Date of Birth
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('employees') AND name = 'date_of_birth')
BEGIN
    ALTER TABLE employees ADD date_of_birth DATE NULL;
END;
GO

-- Nationality
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('employees') AND name = 'nationality')
BEGIN
    ALTER TABLE employees ADD nationality VARCHAR(100) NULL;
END;
GO

-- Race
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('employees') AND name = 'race')
BEGIN
    ALTER TABLE employees ADD race VARCHAR(100) NULL;
END;
GO

-- Gender
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('employees') AND name = 'gender')
BEGIN
    ALTER TABLE employees ADD gender VARCHAR(50) NULL;
END;
GO

-- Marital Status
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('employees') AND name = 'marital_status')
BEGIN
    ALTER TABLE employees ADD marital_status VARCHAR(50) NULL;
END;
GO

-- NRC (National Registration Card)
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('employees') AND name = 'nrc')
BEGIN
    ALTER TABLE employees ADD nrc VARCHAR(100) NULL;
END;
GO
