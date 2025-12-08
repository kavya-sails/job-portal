package com.jobportal.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Basic Profile Info
    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column
    private LocalDate dob;

    @Column(length = 255)
    private String address;

    @Column(length = 15)
    private String phone;

    @Column(length = 100)
    private String highestEducation;

    @Column(length = 500)
    private String skills;

    @Column
    private Integer experience;

    @Column(length = 100)
    private String resumeUrl;

    @Column
    private LocalDateTime resumeUploadedAt;

    // Soft Delete
    @Column(nullable = false)
    private Boolean isActive = true;

    // Auditing
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Auto Timestamp Handling
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isActive == null) this.isActive = true;
    }

    //Update

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
