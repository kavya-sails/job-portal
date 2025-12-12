package com.job_portal.job_service.controller.command;

import com.job_portal.job_service.dto.command.ApplicationStatusResponseDto;
import com.job_portal.job_service.dto.command.ApplicationStatusUpdateDto;
import com.job_portal.job_service.dto.query.ApplicationHistoryQueryDto;
import com.job_portal.job_service.entity.ApplicationStatus;
import com.job_portal.job_service.exception.MissingUserIdHeaderException;
import com.job_portal.job_service.service.command.ApplicationCommandService;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationCommandControllerTest {

    @Mock
    private ApplicationCommandService commandService;

    @InjectMocks
    private ApplicationCommandController controller;

    @Test
    void apply_withValidHeader_callsService_andReturnsDto() {
        // Build DTO expected from service
        ApplicationHistoryQueryDto dto = ApplicationHistoryQueryDto.builder()
                .applicationId(1L)
                .jobId(2L)
                .jobTitle("J")
                .companyName("C")
                .appliedDate(Instant.now())
                .status(ApplicationStatus.PENDING)
                .build();

        // Controller passes (userId, jobId) to service, so stub accordingly
        when(commandService.apply(1L, 2L)).thenReturn(dto);

        var resp = controller.apply(2L, 1L); // jobId = 2L, userId = 1L
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).isEqualTo(dto);

        verify(commandService).apply(1L, 2L);
    }

    @Test
    void apply_missingHeader_null_throwsBadRequest() {
        // Only null header should throw BadRequestException (controller behaviour)
        assertThrows(MissingUserIdHeaderException.class, () -> controller.apply(2L, null));
    }

    @Test
    void apply_withDifferentNonNullHeader_callsService_andReturnsDto() {
        // If controller accepts non-null user ids (e.g. 18L), stub service and assert success
        ApplicationHistoryQueryDto dto = ApplicationHistoryQueryDto.builder()
                .applicationId(11L)
                .jobId(2L)
                .jobTitle("JobX")
                .companyName("CompanyX")
                .appliedDate(Instant.now())
                .status(ApplicationStatus.PENDING)
                .build();

        // stub for userId = 18L, jobId = 2L (note order: userId, jobId)
        when(commandService.apply(18L, 2L)).thenReturn(dto);

        var resp = controller.apply(2L, 18L); // jobId = 2L, userId = 18L
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).isEqualTo(dto);

        verify(commandService).apply(18L, 2L);
    }

    @Test
    void updateStatus_callsService_andReturnsStatusDto() {
        ApplicationStatusUpdateDto request = ApplicationStatusUpdateDto.builder()
                .status(ApplicationStatus.REVIEWED).build();

        ApplicationStatusResponseDto respDto = ApplicationStatusResponseDto.builder()
                .applicationId(5L).status(ApplicationStatus.REVIEWED).build();

        // controller calls commandService.updateStatus(applicationId, status)
        when(commandService.updateStatus(5L, ApplicationStatus.REVIEWED)).thenReturn(respDto);

        var response = controller.updateStatus(5L, request);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().getStatus()).isEqualTo(ApplicationStatus.REVIEWED);

        verify(commandService).updateStatus(5L, ApplicationStatus.REVIEWED);
    }
}