package com.job_portal.job_service.controller.command;

import com.job_portal.job_service.dto.command.JobCommandDto;
import com.job_portal.job_service.dto.query.JobDetailsQueryDto;
import com.job_portal.job_service.exception.GlobalExceptionHandler;
import com.job_portal.job_service.service.command.JobCommandService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobCommandControllerTest {

    @Mock
    private JobCommandService jobCommandService;

    @InjectMocks
    private JobCommandController controller;

    // real exception handler instance (we call it directly)
    private GlobalExceptionHandler globalExceptionHandler;

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void createJob_valid_invokesService_andReturnsDto() {
        // Build a DTO that satisfies bean validation constraints
        JobCommandDto dto = JobCommandDto.builder()
                .title("Valid Title")
                .description("This description has at least twenty characters.")
                .location("Bengaluru")
                .experienceRequired(3)
                .companyName("Company Inc")
                .expiryDays(10)
                .education("B.Tech in CS")
                .skills("Java, Spring Boot")
                .packageOffered("10 LPA")
                .build();

        JobDetailsQueryDto returned = JobDetailsQueryDto.builder()
                .jobId(5L)
                .title(dto.getTitle())
                .location(dto.getLocation())
                .build();

        when(jobCommandService.createJob(any(JobCommandDto.class))).thenReturn(returned);

        // Validate DTO first - should be no violations
        Set<ConstraintViolation<JobCommandDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();

        // Call controller directly (since DTO valid)
        var resp = controller.createJob(dto);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).isEqualTo(returned);

        verify(jobCommandService, times(1)).createJob(any(JobCommandDto.class));
    }

    @Test
    void updateJob_invokesService_andReturnsDto() {
        JobCommandDto dto = JobCommandDto.builder().title("T").build();
        JobDetailsQueryDto returned = JobDetailsQueryDto.builder().jobId(2L).title("T").build();
        when(jobCommandService.updateJob(2L, dto)).thenReturn(returned);

        var resp = controller.updateJob(2L, dto);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).isEqualTo(returned);
        verify(jobCommandService).updateJob(2L, dto);
    }

    @Test
    void deleteJob_invokesService_andReturnsNoContent() {
        doNothing().when(jobCommandService).deleteJob(3L);
        var resp = controller.deleteJob(3L);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getStatusCode().value()).isEqualTo(204);
        verify(jobCommandService).deleteJob(3L);
    }
}