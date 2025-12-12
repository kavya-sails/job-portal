package com.job_portal.job_service.service.command;

import com.job_portal.job_service.entity.ApplicationEntity;
import com.job_portal.job_service.entity.ApplicationStatus;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.publisher.ApplicationStatusPublisher;
import com.job_portal.job_service.repository.command.ApplicationCommandRepository;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

class ApplicationCommandServiceTest {

    @Mock
    private ApplicationCommandRepository applicationRepo;

    @Mock
    private JobQueryRepository jobRepo;

    // **mock the publisher** so publish(...) doesn't perform real operations
    @Mock
    private ApplicationStatusPublisher statusPublisher;

    @InjectMocks
    private ApplicationCommandService service;

    @BeforeEach
    void init() { MockitoAnnotations.openMocks(this); }

    @Test
    void apply_jobNotFound_throws() {
        when(jobRepo.findById(5L)).thenReturn(Optional.empty());

        Throwable t = catchThrowable(() -> service.apply(1L, 5L));
        assertThat(t).isInstanceOf(RuntimeException.class); // or JobNotFoundException if you import it
    }

    @Test
    void apply_alreadyApplied_throwsConflict() {
        JobEntity j = JobEntity.builder().jobId(10L).companyName("C").build();
        when(jobRepo.findById(10L)).thenReturn(Optional.of(j));
        when(applicationRepo.findByJobJobIdAndUserId(10L, 1L))
                .thenReturn(Optional.of(ApplicationEntity.builder().applicationId(2L).job(j).userId(3L).build()));

        Throwable t = catchThrowable(() -> service.apply(2L, 10L));
        assertThat(t).isInstanceOf(RuntimeException.class); // or ApplicationConflictException
    }

    @Test
    void apply_success_savesAndReturns() {
        JobEntity j = JobEntity.builder().jobId(10L).companyName("C").build();
        when(jobRepo.findById(10L)).thenReturn(Optional.of(j));
        when(applicationRepo.findByJobJobIdAndUserId(10L, 1L)).thenReturn(Optional.empty());

        ArgumentCaptor<ApplicationEntity> captor = ArgumentCaptor.forClass(ApplicationEntity.class);
        ApplicationEntity saved = ApplicationEntity.builder().applicationId(100L).job(j).userId(1L).status(ApplicationStatus.PENDING).build();
        when(applicationRepo.save(captor.capture())).thenReturn(saved);

        var result = service.apply(1L, 10L);
        assertThat(result.getApplicationId()).isEqualTo(100L);
        assertThat(captor.getValue().getUserId()).isEqualTo(1L);
    }

    @Test
    void updateStatus_notFound_throws() {
        when(applicationRepo.findById(999L)).thenReturn(Optional.empty());
        Throwable t = catchThrowable(() -> service.updateStatus(999L, ApplicationStatus.SELECTED));
        assertThat(t).isInstanceOf(RuntimeException.class); // or ApplicationNotFoundException
    }

    @Test
    void updateStatus_success_updatesAndReturns() {
        // <-- important: include a JobEntity on the ApplicationEntity
        JobEntity j = JobEntity.builder().jobId(10L).title("SWE").companyName("Acme").build();

        ApplicationEntity existing = ApplicationEntity.builder()
                .applicationId(5L)
                .job(j)                                  // <<-- set job so getJob() != null
                .userId(1L)
                .status(ApplicationStatus.PENDING)
                .build();

        when(applicationRepo.findById(5L)).thenReturn(Optional.of(existing));
        when(applicationRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        var resp = service.updateStatus(5L, ApplicationStatus.SELECTED);

        assertThat(resp.getStatus()).isEqualTo(ApplicationStatus.SELECTED);
        verify(applicationRepo).save(existing);
        // verify that publisher was called
        verify(statusPublisher).publish(any());
    }
}