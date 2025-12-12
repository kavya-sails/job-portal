package com.job_portal.job_service.controller.query;

import com.job_portal.job_service.dto.query.ApplicationHistoryQueryDto;
import com.job_portal.job_service.exception.ForbiddenException;
import com.job_portal.job_service.service.query.ApplicationQueryService;
import jakarta.ws.rs.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationQueryControllerUnitTest {

    @Mock
    private ApplicationQueryService queryService;

    @InjectMocks
    private ApplicationQueryController controller;

    // NO @BeforeEach openMocks — MockitoExtension handles it

    @Test
    void getHistory_userMatchesHeader_returnsList() {
        var dto = ApplicationHistoryQueryDto.builder().applicationId(1L).build();
        when(queryService.getApplicationsByUser("user-1")).thenReturn(List.of(dto));

        var resp = controller.getHistory("user-1", "user-1", "USER");
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).hasSize(1);

        verify(queryService).getApplicationsByUser("user-1");
    }

    @Test
    void getHistory_adminCanQueryOtherUser() {
        var dto = ApplicationHistoryQueryDto.builder().applicationId(2L).build();
        when(queryService.getApplicationsByUser("someone")).thenReturn(List.of(dto));

        var resp = controller.getHistory("someone", "admin-1", "ADMIN");
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).hasSize(1);
    }

    @Test
    void getHistory_missingHeader_throwsBadRequest() {
        assertThrows(BadRequestException.class, () -> controller.getHistory("u", "", "USER"));
        assertThrows(BadRequestException.class, () -> controller.getHistory("u", null, "USER"));
    }

    @Test
    void getHistory_forbidden_whenDifferentUserAndNotAdmin() {
        assertThrows(ForbiddenException.class, () -> controller.getHistory("other", "me", "USER"));
    }
}