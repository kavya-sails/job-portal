-- V3__create_user_profile.sql

CREATE TABLE user_profile (
    id                              BIGINT NOT NULL,
    first_name                      VARCHAR(50) NOT NULL,
    last_name                       VARCHAR(50) NOT NULL,
    dob                             DATE NULL,
    address                         VARCHAR(255) NULL,
    phone                           VARCHAR(15) NULL,
    skills                          VARCHAR(500) NULL,
    experience                      INT NULL,
    job_role                        VARCHAR(50) NULL,
    experience_level                VARCHAR(50) NULL,
    profile_completion_percentage   INT NULL,
    resume_url                      VARCHAR(255) NULL,
    resume_uploaded_at              DATETIME NULL,
    portfolio_url                   VARCHAR(255) NULL,
    linkedin_url                    VARCHAR(255) NULL,
    created_at                      DATETIME NOT NULL,
    updated_at                      DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_profile_auth_user
        FOREIGN KEY (id)
        REFERENCES auth_users (user_id)
        ON DELETE CASCADE
) ENGINE=InnoDB;
