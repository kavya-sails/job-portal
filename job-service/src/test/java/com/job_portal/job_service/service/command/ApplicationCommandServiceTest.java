package com.job_portal.job_service.service.command;

import com.job_portal.job_service.entity.ApplicationEntity;
import com.job_portal.job_service.entity.ApplicationStatus;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.publisher.ApplicationStatusPublisher;
import com.job_portal.job_service.repository.command.ApplicationCommandRepository;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationCommandServiceTest {

    @Mock
    private ApplicationCommandRepository applicationRepo;

    @Mock
    private JobQueryRepository jobRepo;

    @Mock
    private ApplicationStatusPublisher statusPublisher;

    @InjectMocks
    private ApplicationCommandService service;

    @Test
    void apply_jobNotFound_throws() {
        // jobId = 5 does not exist
        when(jobRepo.findById(5L)).thenReturn(Optional.empty());

        Throwable t = catchThrowable(() -> service.apply(1L, 5L)); // userId = 1, jobId = 5
        assertThat(t).isInstanceOf(RuntimeException.class); // replace with JobNotFoundException if available
    }

    @Test
    void apply_alreadyApplied_throwsConflict() {
        // Arrange: job exists and an application already exists for userId = 1 and jobId = 10
        JobEntity j = JobEntity.builder().jobId(10L).companyName("C").build();
        when(jobRepo.findById(10L)).thenReturn(Optional.of(j));

        // applicationRepo should return an existing application for jobId=10 and userId=1
        when(applicationRepo.findByJobJobIdAndUserId(10L, 1L))
                .thenReturn(Optional.of(
                        ApplicationEntity.builder()
                                .applicationId(2L)
                                .job(j)
                                .userId(1L)   // existing application belongs to userId = 1
                                .build()
                ));

        // Act: call service with userId = 1, jobId = 10 (match the stub)
        Throwable t = catchThrowable(() -> service.apply(1L, 10L));
        assertThat(t).isInstanceOf(RuntimeException.class); // replace with ApplicationConflictException if available
    }

    @Test
    void apply_success_savesAndReturns() {
        // Arrange: job exists and no existing application for userId = 1
        JobEntity j = JobEntity.builder().jobId(10L).companyName("C").build();
        when(jobRepo.findById(10L)).thenReturn(Optional.of(j));
        when(applicationRepo.findByJobJobIdAndUserId(10L, 1L)).thenReturn(Optional.empty());

        ArgumentCaptor<ApplicationEntity> captor = ArgumentCaptor.forClass(ApplicationEntity.class);
        ApplicationEntity saved = ApplicationEntity.builder()
                .applicationId(100L)
                .job(j)
                .userId(1L)
                .status(ApplicationStatus.PENDING)
                .build();
        when(applicationRepo.save(captor.capture())).thenReturn(saved);

        // Act
        var result = service.apply(1L, 10L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getApplicationId()).isEqualTo(100L);
        assertThat(captor.getValue().getUserId()).isEqualTo(1L);
        verify(applicationRepo).save(any(ApplicationEntity.class));
    }

    @Test
    void updateStatus_notFound_throws() {
        when(applicationRepo.findById(999L)).thenReturn(Optional.empty());
        Throwable t = catchThrowable(() -> service.updateStatus(999L, ApplicationStatus.SELECTED));
        assertThat(t).isInstanceOf(RuntimeException.class); // or ApplicationNotFoundException
    }

    @Test
    void updateStatus_success_updatesAndReturns() {
        // Prepare a job and an application that references it
        JobEntity j = JobEntity.builder().jobId(10L).title("SWE").companyName("Acme").build();

        ApplicationEntity existing = ApplicationEntity.builder()
                .applicationId(5L)
                .job(j)
                .userId(1L)
                .status(ApplicationStatus.PENDING)
                .build();

        when(applicationRepo.findById(5L)).thenReturn(Optional.of(existing));
        when(applicationRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        var resp = service.updateStatus(5L, ApplicationStatus.SELECTED);

        assertThat(resp).isNotNull();
        assertThat(resp.getStatus()).isEqualTo(ApplicationStatus.SELECTED);
        verify(applicationRepo).save(existing);
        verify(statusPublisher).publish(any());
    }
}