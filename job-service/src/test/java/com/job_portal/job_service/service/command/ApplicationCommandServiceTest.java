package com.job_portal.job_service.service.command;

import com.job_portal.job_service.client.UserClient;
import com.job_portal.job_service.dto.command.ApplicationStatusResponseDto;
import com.job_portal.job_service.dto.event.ApplicationStatusEvent;
import com.job_portal.job_service.dto.query.ApplicationHistoryQueryDto;
import com.job_portal.job_service.entity.ApplicationEntity;
import com.job_portal.job_service.entity.ApplicationStatus;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.exception.ApplicationConflictException;
import com.job_portal.job_service.exception.ApplicationNotFoundException;
import com.job_portal.job_service.exception.JobNotFoundException;
import com.job_portal.job_service.exception.MessagePublishException;
import com.job_portal.job_service.publisher.ApplicationStatusPublisher;
import com.job_portal.job_service.repository.command.ApplicationCommandRepository;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import java.time.Instant;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ApplicationCommandServiceTest {

    @Mock
    private ApplicationCommandRepository applicationCommandRepository;

    @Mock
    private JobQueryRepository jobQueryRepository;

    @Mock
    private ApplicationStatusPublisher statusPublisher;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private ApplicationCommandService applicationCommandService;

    @BeforeEach
    void setUp() {
        // global default: make user-client a no-op (void method)
        doNothing().when(userClient).checkUserExists(anyLong());
    }

    @Test
    void apply_whenJobExistsAndNoPriorApplication_shouldSaveAndReturnHistory() {
        Long jobId = 1L;
        Long userId = 11L;
        JobEntity job = JobEntity.builder().jobId(jobId).companyName("Org").title("T").build();
        when(jobQueryRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(applicationCommandRepository.findByJobJobIdAndUserId(jobId, userId)).thenReturn(Optional.empty());

        ApplicationEntity saved = ApplicationEntity.builder()
                .applicationId(200L)
                .job(job)
                .userId(userId)
                .companyName("Org")
                .appliedDate(Instant.now())
                .status(ApplicationStatus.PENDING)
                .build();

        when(applicationCommandRepository.save(any(ApplicationEntity.class))).thenReturn(saved);

        ApplicationHistoryQueryDto result = applicationCommandService.apply(userId, jobId);

        assertThat(result).isNotNull();
        assertThat(result.getApplicationId()).isEqualTo(200L);
        assertThat(result.getJobId()).isEqualTo(jobId);
        verify(applicationCommandRepository).findByJobJobIdAndUserId(jobId, userId);
        verify(applicationCommandRepository).save(any(ApplicationEntity.class));
        verify(userClient).checkUserExists(userId);
    }

    @Test
    void apply_whenJobNotFound_shouldThrowJobNotFound() {
        when(jobQueryRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> applicationCommandService.apply(1L, 999L))
                .isInstanceOf(JobNotFoundException.class);
    }

    @Test
    void apply_whenAlreadyApplied_shouldThrowConflict() {
        Long jobId = 2L, userId = 3L;
        JobEntity job = JobEntity.builder().jobId(jobId).build();
        ApplicationEntity existing = ApplicationEntity.builder().applicationId(10L).userId(userId).job(job).build();

        when(jobQueryRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(applicationCommandRepository.findByJobJobIdAndUserId(jobId, userId)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> applicationCommandService.apply(userId, jobId))
                .isInstanceOf(ApplicationConflictException.class);

        verify(applicationCommandRepository).findByJobJobIdAndUserId(jobId, userId);
        verify(applicationCommandRepository, never()).save(any(ApplicationEntity.class));
        verify(userClient).checkUserExists(userId);
    }

    @Test
    void updateStatus_whenApplicationExists_shouldPublishAndReturnStatus() {
        Long appId = 50L;
        JobEntity job = JobEntity.builder().jobId(9L).title("Title").build();
        ApplicationEntity existing = ApplicationEntity.builder()
                .applicationId(appId)
                .job(job)
                .userId(7L)
                .status(ApplicationStatus.PENDING)
                .build();

        when(applicationCommandRepository.findById(appId)).thenReturn(Optional.of(existing));
        when(applicationCommandRepository.save(any(ApplicationEntity.class))).thenAnswer(inv -> {
            ApplicationEntity a = inv.getArgument(0);
            a.setApplicationId(appId);
            return a;
        });

        // publisher does not throw
        doNothing().when(statusPublisher).publish(any(ApplicationStatusEvent.class));

        ApplicationStatusResponseDto resp = applicationCommandService.updateStatus(appId, ApplicationStatus.SELECTED);

        assertThat(resp).isNotNull();
        assertThat(resp.getApplicationId()).isEqualTo(appId);
        assertThat(resp.getStatus()).isEqualTo(ApplicationStatus.SELECTED);
        verify(statusPublisher).publish(any(ApplicationStatusEvent.class));
    }

    @Test
    void updateStatus_whenPublisherThrows_shouldWrapInMessagePublishException() {
        Long appId = 60L;
        JobEntity job = JobEntity.builder().jobId(2L).title("t").build();
        ApplicationEntity existing = ApplicationEntity.builder()
                .applicationId(appId)
                .job(job)
                .userId(8L)
                .status(ApplicationStatus.PENDING)
                .build();

        when(applicationCommandRepository.findById(appId)).thenReturn(Optional.of(existing));
        when(applicationCommandRepository.save(any(ApplicationEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        doThrow(new RuntimeException("broker down")).when(statusPublisher).publish(any(ApplicationStatusEvent.class));

        assertThatThrownBy(() -> applicationCommandService.updateStatus(appId, ApplicationStatus.REJECTED))
                .isInstanceOf(MessagePublishException.class)
                .hasMessageContaining("broker down");

        verify(statusPublisher).publish(any(ApplicationStatusEvent.class));
    }

    @Test
    void updateStatus_whenApplicationNotFound_shouldThrow() {
        when(applicationCommandRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> applicationCommandService.updateStatus(999L, ApplicationStatus.SELECTED))
                .isInstanceOf(ApplicationNotFoundException.class);
    }

    // Example test to simulate user-service unavailable (override default doNothing):
    @Test
    void apply_whenUserServiceUnavailable_shouldWrapOrPropagate() {
        Long jobId = 1L;
        Long userId = 21L;
        JobEntity job = JobEntity.builder().jobId(jobId).companyName("Org").title("T").build();
        when(jobQueryRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(applicationCommandRepository.findByJobJobIdAndUserId(jobId, userId)).thenReturn(Optional.empty());

        // simulate user-service failure (UserClient.checkUserExists throws)
        doThrow(new RuntimeException("user-service unavailable")).when(userClient).checkUserExists(userId);

        // adjust expected behavior depending on how your service handles the runtime exception.
        assertThatThrownBy(() -> applicationCommandService.apply(userId, jobId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("user-service unavailable");

        verify(userClient).checkUserExists(userId);
        verify(applicationCommandRepository, never()).save(any(ApplicationEntity.class));
    }
}