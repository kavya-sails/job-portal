//package com.job_portal.job_service.entity;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.Instant;
//@Entity
//@Table(name = "jobs")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
////@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
//public class JobEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "job_id")
//    private Long jobId;
//
//    private String title;
//
//    @Column(columnDefinition = "text")
//    private String description;
//
//    private String location;
//
//    @Column(name = "experience_required")
//    private Integer experienceRequired;
//
//    @Column(name = "posted_date")
//    private Instant postedDate;
//
//    @Column(name = "expires_at")
//    private Instant expiresAt;
//}

package com.job_portal.job_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class JobEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long jobId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "text", nullable = false)
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(name = "experience_required", nullable = false)
    private Integer experienceRequired;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "package_offered", nullable = false)
    private String packageOffered;

    /**
     * Stored as free text (comma separated / newline) for now.
     * If you want structured skills, convert to @ElementCollection or a separate table.
     */
    @Column(columnDefinition = "text", nullable = false)
    private String skills;

    @Column(columnDefinition = "text", nullable = false)
    private String education;

    @Column(name = "posted_date", nullable = false)
    private Instant postedDate;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}