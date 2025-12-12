package com.job_portal.job_service.controller.command;

import com.job_portal.job_service.dto.command.JobCommandDto;
import com.job_portal.job_service.dto.query.JobDetailsQueryDto;
import com.job_portal.job_service.exception.GlobalExceptionHandler;
..........................................................................import com.job_portal.job_service.service.command.JobCommandService;
import com.job_portal.job_service.testutils.ValidationTestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
        // MockitoExtension initializes mocks; do NOT call openMocks(this)
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void createJob_valid_invokesService_andReturnsDto() {
        JobCommandDto dto = JobCommandDto.builder()
                .title("Valid Title")
                .description("This description has at least twenty characters.")
                .location("Bengaluru")
                .experienceRequired(3)
                .companyName("C")
                .expiryDays(10)
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

//    @Test
//    void createJob_invalidDto_handledByGlobalExceptionHandler_returnsValidationResponse() throws Exception {
//        JobCommandDto bad = JobCommandDto.builder()
//                .title("") // invalid: NotBlank
//                .description("short") // invalid: too short
//                .location("L")
//                .experienceRequired(1)
//                .companyName("C")
//                .build();
//
//        // Validate DTO using validator
//        Set<ConstraintViolation<JobCommandDto>> violations = validator.validate(bad);
//        assertThat(violations).isNotEmpty();
//
//        // Build MethodArgumentNotValidException that mimics Spring MVC behavior
//        // ValidationTestUtils.buildMethodArgNotValidException uses BeanPropertyBindingResult and rejects fields
//        @SuppressWarnings("unchecked")
//        MethodArgumentNotValidException ex = ValidationTestUtils.buildMethodArgNotValidException(bad, (Set) violations);
//
//        // mock HttpServletRequest so handler.getRequestURI() won't NPE
//        HttpServletRequest req = mock(HttpServletRequest.class);
//        when(req.getRequestURI()).thenReturn("/api/jobs");
//
//        // Call the handler with the mocked request
//        var responseEntity = globalExceptionHandler.handleValidation(ex, req);
//
//        assertThat(responseEntity.getStatusCode().is4xxClientError()).isTrue();
//        ApiErrorResponse body = responseEntity.getBody();
//        assertThat(body).isNotNull();
//        assertThat(body.getCode()).isEqualTo("VALIDATION_FAILED");
//        assertThat(body.getErrors()).isNotEmpty();
//        // ensure a field error exists for title or description
//        assertThat(body.getErrors().keySet()).anyMatch(k -> k.equals("title") || k.equals("description"));
//    }
}