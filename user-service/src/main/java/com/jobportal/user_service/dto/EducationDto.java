package com.jobportal.user_service.dto;

import com.jobportal.user_service.enums.EducationLevel;
import com.jobportal.user_service.enums.Specialisation;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationDto {

    @NotNull(message = "Highest education is required")
    private EducationLevel highestEducation;

    @NotNull(message = "Specialisation is required")
    private Specialisation specialisation;

    @NotBlank(message = "Institute name is required")
    @Size(max = 150, message = "Institute name must not exceed 150 characters")
    private String institute;

    // Location is OPTIONAL now
    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;

    @NotNull(message = "Pass out year is required")
    @Min(value = 1900, message = "Pass out year must be after 1900")
    @Max(value = 2100, message = "Pass out year must be before 2100")
    private Integer passOutYear;

    @NotNull(message = "Percentage is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Percentage cannot be negative")
    @DecimalMax(value = "100.0", inclusive = true, message = "Percentage cannot exceed 100")
    private Double percentage;
}
