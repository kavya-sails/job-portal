CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    dob DATE,
    address VARCHAR(255),
    phone VARCHAR(15),
    highest_education VARCHAR(100),
    skills VARCHAR(500),
    experience INT,
    resume_url VARCHAR(100),
    resume_uploaded_at DATETIME,

    is_active TINYINT(1) NOT NULL DEFAULT 1,

    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);
