package com.jobportal.user_service.dto;

import com.jobportal.user_service.enums.ExperienceLevel;
import com.jobportal.user_service.enums.JobRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfilePartialUpdateDto {

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z\\s'-]{1,49}$",
            message = "First name contains invalid characters"
    )
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z\\s'-]{1,49}$",
            message = "Last name contains invalid characters"
    )
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Pattern(
            regexp = "^(?!([0-9])\\1{9,14})[0-9]{10,15}$",
            message = "Phone number must be 10–15 digits and not all the same digit"
    )
    private String phone;

    @Size(max = 500, message = "Skills must not exceed 500 characters")
    @Pattern(
            regexp = "^[^,]+(,[^,]+)*$",
            message = "Skills must be separated by commas"
    )
    private String skills;

    @Min(value = 0, message = "Experience cannot be negative")
    @Max(value = 50, message = "Experience cannot exceed 50 years")
    private Integer experience;

    private JobRole jobRole;

    private ExperienceLevel experienceLevel;

    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "Resume URL must be a valid URL"
    )
    private String resumeUrl;

    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "Portfolio URL must be a valid URL"
    )
    private String portfolioUrl;

    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "LinkedIn URL must be a valid URL"
    )
    private String linkedinUrl;

    @Valid
    private EducationDto education;
}
