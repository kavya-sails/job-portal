CREATE TABLE user_education (
    user_profile_id    BIGINT PRIMARY KEY,
    highest_education  VARCHAR(50) NOT NULL,
    specialisation     VARCHAR(50) NOT NULL,
    institute          VARCHAR(150) NOT NULL,
    location           VARCHAR(100) NOT NULL,
    pass_out_year      INT NOT NULL,
    percentage         DECIMAL(5,2) NOT NULL,

    CONSTRAINT fk_user_education_profile
        FOREIGN KEY (user_profile_id)
        REFERENCES user_profile (id)
        ON DELETE CASCADE
);