package com.job_portal.job_service.dto.command;

import com.job_portal.job_service.entity.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request body for updating application status")
public class ApplicationStatusUpdateDto {

    @NotNull(message = "Status cannot be null")
    @Schema(
            description = "New status for the application",
            example = "SELECTED",
            required = true
    )
    private ApplicationStatus status;
}