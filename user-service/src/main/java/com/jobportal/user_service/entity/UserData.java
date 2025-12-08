package com.jobportal.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

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

    //  Basic Profile Info
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 10)
    private String phone;

    private String skills;

    //  Resume Upload Fields
    private String resumeUrl;

    private LocalDateTime dob;

    private LocalDateTime resumeUploadedAt;

    //  Soft Delete (Deactivate Account)
    private Boolean isActive = true;

    //  Auditing
    private LocalDateTime createdAt;


    // dfghjhkjkjhjhjhjjh
    private LocalDateTime updatedAt;

    //  Auto Timestamp Handling
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
