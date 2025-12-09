package com.jobportal.user_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPartialUpdateDto {

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

    @Email(message = "Invalid email format")
    private String email;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Pattern(
            // blocks all same-digit numbers like 1111111111, 0000000000, etc.
            regexp = "^(?!([0-9])\\1{9,14})[0-9]{10,15}$",
            message = "Phone number must be 10–15 digits and not all the same digit"
    )
    private String phone;

    @Size(max = 100, message = "Highest education must not exceed 100 characters")
    private String highestEducation;

    @Size(max = 500, message = "Skills must not exceed 500 characters")
    private String skills;

    @Min(value = 0, message = "Experience cannot be negative")
    @Max(value = 50, message = "Experience cannot exceed 50 years")
    private Integer experience;

    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "Resume URL must be a valid URL"
    )
    private String resumeUrl;
}
