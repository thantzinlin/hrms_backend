-- ============================================================
-- V6: Drop claim_line_items table (line items removed from claim flow)
-- Run against your existing HRMS database (SQL Server).
-- ============================================================

IF EXISTS (SELECT 1 FROM sys.tables WHERE name = 'claim_line_items')
BEGIN
    DROP TABLE claim_line_items;
END;
GO
