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

    @NotBlank(message = "title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    @Size(min = 20, message = "Description must be at least 20 characters long")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Experience required cannot be null")
    @Min(value = 0, message = "Experience cannot be negative")
    @Max(value = 50, message = "Experience cannot exceed 50 years")
    private Integer experienceRequired;

    /**
     * Company name: required for job postings in most systems — adjust as needed.
     */
    @NotBlank(message = "companyName is required")
    @Size(max = 255, message = "companyName must be at most 255 characters")
    private String companyName;

    /**
     * packageOffered: optional, but cap the length
     */
    @Size(max = 255, message = "packageOffered must be at most 255 characters")
    private String packageOffered;

    /**
     * Skills: free text list. Enforce a reasonable length.
     * If you want structured skills validation (like CSV or JSON array), apply custom validator.
     */
    @Size(max = 2000, message = "skills cannot exceed 2000 characters")
    private String skills;

    /**
     * Education: free text (e.g. "B.Tech, M.Tech, MBA" or details)
     */
    @Size(max = 2000, message = "education cannot exceed 2000 characters")
    private String education;

    @PastOrPresent(message = "postedDate cannot be in the future")
    private Instant postedDate;

    @Min(value = 1, message = "Expiry days must be at least 1")
    @Max(value = 3650, message = "expiryDays seems too large")
    private Integer expiryDays;
}