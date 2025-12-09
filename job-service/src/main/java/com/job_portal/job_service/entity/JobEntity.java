package com.job_portal.job_service.entity;

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
public class JobEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long jobId;

    private String title;

    @Column(columnDefinition = "text")
    private String description;

    private String location;

    @Column(name = "experience_required")
    private Integer experienceRequired;

    @Column(name = "posted_date")
    private Instant postedDate;
}
