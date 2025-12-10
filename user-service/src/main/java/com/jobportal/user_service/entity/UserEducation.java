package com.jobportal.user_service.entity;

import com.jobportal.user_service.enums.EducationLevel;
import com.jobportal.user_service.enums.Specialisation;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "user_education")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEducation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One-to-one with UserProfile
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false, unique = true)
    private UserProfile userProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "highest_education", nullable = false, length = 50)
    private EducationLevel highestEducation;

    @Enumerated(EnumType.STRING)
    @Column(name = "specialisation", nullable = false, length = 50)
    private Specialisation specialisation;

    @Column(name = "institute", nullable = false, length = 150)
    private String institute;

    @Column(name = "location", nullable = false, length = 100)
    private String location;

    @Column(name = "pass_out_year", nullable = false)
    private Integer passOutYear;

    @Column(name = "percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;

}
