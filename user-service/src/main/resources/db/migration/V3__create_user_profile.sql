-- V3__create_user_profile.sql

CREATE TABLE user_profile (
    id                  BIGINT NOT NULL,
    first_name          VARCHAR(50) NOT NULL,
    last_name           VARCHAR(50) NOT NULL,
    dob                 DATE NULL,
    address             VARCHAR(255) NULL,
    phone               VARCHAR(15) NULL,
    highest_education   VARCHAR(100) NULL,
    skills              VARCHAR(500) NULL,
    experience          INT NULL,
    resume_url          VARCHAR(100) NULL,
    resume_uploaded_at  DATETIME NULL,
    is_active           TINYINT(1) NOT NULL DEFAULT 1,
    created_at          DATETIME NOT NULL,
    updated_at          DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_profile_auth_user
        FOREIGN KEY (id)
        REFERENCES auth_users (user_id)
        ON DELETE CASCADE
) ENGINE=InnoDB;
