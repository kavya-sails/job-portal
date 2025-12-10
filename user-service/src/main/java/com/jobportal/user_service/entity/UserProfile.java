package com.jobportal.user_service.entity;

import com.jobportal.user_service.enums.ExperienceLevel;
import com.jobportal.user_service.enums.JobRole;
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
    @Column(name = "id")
    private Long id;

    // -------------------- BASIC INFO --------------------

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "skills", length = 500)
    private String skills;

    @Column(name = "experience")
    private Integer experience;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_role", length = 50)
    private JobRole jobRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", length = 50)
    private ExperienceLevel experienceLevel;

    @Column(name = "profile_completion_percentage")
    private Integer profileCompletionPercentage;

    // -------------------- RESUME & LINKS --------------------

    @Column(name = "resume_url", length = 255)
    private String resumeUrl;

    @Column(name = "resume_uploaded_at")
    private LocalDateTime resumeUploadedAt;

    @Column(name = "portfolio_url", length = 255)
    private String portfolioUrl;

    @Column(name = "linkedin_url", length = 255)
    private String linkedinUrl;

    // -------------------- EDUCATION --------------------

    @OneToOne(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserEducation education;

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
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
