package com.jobportal.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -------------------- BASIC INFO --------------------

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "highest_education", length = 100)
    private String highestEducation;

    @Column(name = "skills", length = 500)
    private String skills;

    @Column(name = "experience")
    private Integer experience;

    // -------------------- RESUME --------------------

    @Column(name = "resume_url", length = 100)
    private String resumeUrl;

    @Column(name = "resume_uploaded_at")
    private LocalDateTime resumeUploadedAt;

    // -------------------- STATUS --------------------

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // -------------------- AUDIT --------------------

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // -------------------- LIFECYCLE CALLBACKS --------------------

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
