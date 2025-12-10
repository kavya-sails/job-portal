-- V2__create_auth_users.sql


CREATE TABLE auth_users (
    user_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role_id     BIGINT NOT NULL,
    is_active   TINYINT(1) NOT NULL DEFAULT 1,
    created_at  DATETIME NULL,
    updated_at  DATETIME NULL,
    CONSTRAINT fk_auth_users_role
        FOREIGN KEY (role_id)
        REFERENCES roles (id)
) ENGINE=InnoDB;