-- Drop the old column completely
ALTER TABLE applications
    DROP COLUMN IF EXISTS status;

-- Add new varchar column with default
ALTER TABLE applications
    ADD COLUMN status VARCHAR(20) DEFAULT 'PENDING';
