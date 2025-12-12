CREATE TABLE user_profile (
    id                  BIGINT PRIMARY KEY,
    first_name          VARCHAR(50) NOT NULL,
    last_name           VARCHAR(50) NOT NULL,
    dob                 DATE,
    address             VARCHAR(255),
    phone               VARCHAR(15),
    highest_education   VARCHAR(100),
    skills              VARCHAR(500),
    experience          INT,
    resume_url          VARCHAR(100),
    resume_uploaded_at  TIMESTAMP,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_profile_credentials
        FOREIGN KEY (id)
        REFERENCES credentials (user_id)
        ON DELETE CASCADE
);