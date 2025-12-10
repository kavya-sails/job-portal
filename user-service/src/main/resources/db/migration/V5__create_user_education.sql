CREATE TABLE user_education (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_profile_id    BIGINT NOT NULL,
    highest_education  VARCHAR(50) NOT NULL,
    specialisation     VARCHAR(50) NOT NULL,
    institute          VARCHAR(150) NOT NULL,
    location           VARCHAR(100) NOT NULL,
    pass_out_year      INT NOT NULL,
    percentage         DECIMAL(5,2) NOT NULL,

    CONSTRAINT uq_user_education_user_profile UNIQUE (user_profile_id),

    CONSTRAINT fk_user_education_profile
        FOREIGN KEY (user_profile_id)
        REFERENCES user_profile (id)
        ON DELETE CASCADE
) ENGINE=InnoDB;
