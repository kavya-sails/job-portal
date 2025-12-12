-- V1_1__create_job_and_application_tables.sql
CREATE TABLE jobs (
                      job_id BIGSERIAL PRIMARY KEY,

                      title VARCHAR(255) NOT NULL,
                      description TEXT,
                      location VARCHAR(255),
                      experience_required INT,

                      company_name VARCHAR(255) NOT NULL,
                      package_offered VARCHAR(255),
                      skills TEXT,
                      education TEXT,

                      posted_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
                      expires_at TIMESTAMP WITHOUT TIME ZONE,

                      CONSTRAINT chk_exp_required CHECK (experience_required >= 0)
);

CREATE INDEX idx_jobs_title ON jobs (LOWER(title));
CREATE INDEX idx_jobs_location ON jobs (LOWER(location));
CREATE INDEX idx_jobs_company ON jobs (LOWER(company_name));
CREATE INDEX idx_jobs_expiry ON jobs (expires_at);

CREATE TABLE applications (
                              application_id BIGSERIAL PRIMARY KEY,
                              job_id BIGINT NOT NULL REFERENCES jobs(job_id) ON DELETE CASCADE,
                              user_id BIGINT NOT NULL,
                              company_name VARCHAR(255) NOT NULL,

                               applied_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),

                              status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                              CONSTRAINT uq_job_user UNIQUE (job_id, user_id)
);


CREATE INDEX idx_applications_user ON applications (user_id);
CREATE INDEX idx_applications_job ON applications (job_id);
CREATE INDEX idx_applications_status ON applications (status);