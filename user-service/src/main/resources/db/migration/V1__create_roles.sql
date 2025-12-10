-- V1__create_roles.sql

CREATE TABLE roles (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name   VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

