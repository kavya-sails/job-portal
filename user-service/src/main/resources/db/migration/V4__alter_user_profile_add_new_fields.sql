ALTER TABLE user_profile
    DROP COLUMN highest_education,
    DROP COLUMN is_active,
    ADD COLUMN job_role VARCHAR(50) NULL,
    ADD COLUMN experience_level VARCHAR(50) NULL,
    ADD COLUMN profile_completion_percentage INT NULL,
    ADD COLUMN portfolio_url VARCHAR(255) NULL,
    ADD COLUMN linkedin_url VARCHAR(255) NULL,
    MODIFY COLUMN resume_url VARCHAR(255) NULL;
