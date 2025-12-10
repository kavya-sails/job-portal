package com.jobportal.user_service.dto;

import com.jobportal.user_service.enums.ExperienceLevel;
import com.jobportal.user_service.enums.JobRole;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    private LocalDate dob;
    private String address;
    private String phone;

    private String skills;
    private Integer experience; // in years

    private JobRole jobRole;
    private ExperienceLevel experienceLevel;

    private Integer profileCompletionPercentage;

    private String resumeUrl;
    private LocalDateTime resumeUploadedAt;

    private String portfolioUrl;
    private String linkedinUrl;

    private EducationDto education;
}
