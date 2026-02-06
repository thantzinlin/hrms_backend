-- Position feature: create positions table and add position_id to employees
-- Run this after your existing schema (employees, departments, etc.) is in place.

-- 1. Create positions table (aligned with BaseEntity + Position fields)
CREATE TABLE positions (
    position_id   BIGINT IDENTITY(1,1) NOT NULL,
    position_name VARCHAR(255) NOT NULL,
    description   VARCHAR(500),
    is_active     BIT NOT NULL DEFAULT 1,
    version       INT NOT NULL DEFAULT 0,
    is_deleted    BIT NOT NULL DEFAULT 0,
    created_by    VARCHAR(255),
    update_by     VARCHAR(255),
    created_at    DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at    DATETIME2 NOT NULL DEFAULT GETDATE(),

    CONSTRAINT pk_positions PRIMARY KEY (position_id)
);

-- 2. Add position_id to employees (FK to positions)
ALTER TABLE employees
ADD position_id BIGINT NULL;

ALTER TABLE employees
ADD CONSTRAINT fk_employee_position
    FOREIGN KEY (position_id)
    REFERENCES positions(position_id);

-- Optional: migrate existing position text to positions table and link (one-time)
-- Uncomment and adjust if you want to backfill from existing employees.position (VARCHAR) data:
/*
INSERT INTO positions (position_name, description, is_active, created_by)
SELECT DISTINCT position, NULL, 1, 'migration'
FROM employees
WHERE position IS NOT NULL AND position <> '';

UPDATE e
SET e.position_id = p.position_id
FROM employees e
INNER JOIN positions p ON p.position_name = e.position
WHERE e.position IS NOT NULL AND e.position <> '';
*/

-- Optional: drop legacy position column after migration (only if you no longer need the varchar column)
-- ALTER TABLE employees DROP COLUMN position;
