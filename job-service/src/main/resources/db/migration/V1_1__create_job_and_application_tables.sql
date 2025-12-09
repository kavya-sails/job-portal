-- V1_1__create_job_and_application_tables.sql
CREATE TABLE IF NOT EXISTS jobs (
    job_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    location VARCHAR(255),
    experience_required INT,
    posted_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

CREATE TYPE application_status AS ENUM ('PENDING','REVIEWED','SELECTED','REJECTED');

CREATE TABLE IF NOT EXISTS applications (
    application_id BIGSERIAL PRIMARY KEY,
    job_id BIGINT NOT NULL REFERENCES jobs(job_id) ON DELETE CASCADE,
    user_id VARCHAR(100) NOT NULL,
    applied_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    status application_status DEFAULT 'PENDING'
);