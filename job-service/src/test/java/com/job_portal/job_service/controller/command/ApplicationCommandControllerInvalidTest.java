package com.job_portal.job_service.controller.command;

import com.job_portal.job_service.dto.command.ApplicationStatusUpdateDto;
import com.job_portal.job_service.exception.InvalidApplicationStatusException;
import com.job_portal.job_service.exception.MissingUserIdHeaderException;
import com.job_portal.job_service.service.command.ApplicationCommandService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ApplicationCommandControllerInvalidTest {

    @Mock
    private ApplicationCommandService svc;

    @InjectMocks
    private ApplicationCommandController controller;

    @Test
    void apply_nullHeader_throwsMissingUserIdHeaderException() {
        assertThatThrownBy(() -> controller.apply(2L, null))
                .isInstanceOf(MissingUserIdHeaderException.class);
    }

    @Test
    void updateStatus_nullDto_throwsInvalidApplicationStatusException() {
        assertThatThrownBy(() -> controller.updateStatus(10L, null))
                .isInstanceOf(InvalidApplicationStatusException.class)
                .hasMessageContaining("Status is required");
    }

    @Test
    void updateStatus_dtoWithNullStatus_throwsInvalidApplicationStatusException() {
        ApplicationStatusUpdateDto dto = ApplicationStatusUpdateDto.builder().status(null).build();
        assertThatThrownBy(() -> controller.updateStatus(10L, dto))
                .isInstanceOf(InvalidApplicationStatusException.class)
                .hasMessageContaining("Status is required");
    }
}