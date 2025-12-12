//package com.job_portal.job_service.dto.command;
//
//import jakarta.validation.constraints.*;
//import lombok.*;
//
//import java.time.Instant;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class JobCommandDto {
//
//    @NotBlank(message = "title is required")
//    @Size(max = 255, message = "Title must be at most 255 characters long")
//    private String title;
//
//    @Size(max = 5000, message = "Description cannot exceed 5000 characters")
//    private String description;
//
//    @NotBlank(message = "Location is required")
//    @Size(max = 255, message = "location must be at most 255 characters")
//    private String location;
//
//    @Min(value = 0, message = "Experience cannot be negative")
//    private Integer experienceRequired;
//
//    @PastOrPresent(message = "postedDate cannot be in the future")
//    private Instant postedDate;
//
//    @Min(value = 1, message = "Expiry days must be at least 1")
//    @Max(value = 3650, message = "expiryDays seems too large")
//    private Integer expiryDays;
//}

package com.job_portal.job_service.dto.command;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobCommandDto {

    @NotBlank(message = "Job title is required and cannot be empty")
    @Size(min = 3, max = 100, message = "Job title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Job description is required and cannot be empty")
    @Size(min = 20, max = 5000, message = "Description must be between 20 and 5000 characters")
    private String description;

    @NotBlank(message = "Location is required and cannot be empty")
    @Size(min = 2, max = 255, message = "Location must be between 2 and 255 characters")
    private String location;

    @NotNull(message = "Experience required is mandatory")
    @Min(value = 0, message = "Experience cannot be negative")
    @Max(value = 30, message = "Experience cannot exceed 50 years")
    private Integer experienceRequired;

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 255, message = "Company name must be between 2 and 255 characters")
    private String companyName;

    @NotBlank(message = "packageOffered is required and cannot be empty")
    @Size(max = 255, message = "Package offered must be at most 255 characters")
    private String packageOffered;

    @NotBlank(message = "Skills field is required (recruiter must provide relevant skills)")
    @Size(min = 5, max = 2000, message = "Skills must be between 5 and 2000 characters")
    private String skills;

    @NotBlank(message = "Education details are required")
    @Size(min = 3, max = 2000, message = "Education must be between 3 and 2000 characters")
    private String education;

    @PastOrPresent(message = "Posted date cannot be in the future")
    private Instant postedDate;

    @NotNull(message = "Expiry days field is required")
    @Min(value = 1, message = "Expiry days must be at least 1")
    @Max(value = 90, message = "Expiry days cannot exceed 10 years (3650 days)")
    private Integer expiryDays;
}