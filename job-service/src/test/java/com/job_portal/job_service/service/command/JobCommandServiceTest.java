package com.job_portal.job_service.service.command;

import com.job_portal.job_service.dto.command.JobCommandDto;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.exception.JobNotFoundException;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JobCommandServiceTest {

    @Mock
    private JobQueryRepository jobRepo;

    @InjectMocks
    private JobCommandService service;

    @BeforeEach
    void init() { MockitoAnnotations.openMocks(this); }

    @Test
    void createJob_savesAndReturnsDto() {
        JobCommandDto dto = JobCommandDto.builder()
                .title("T")
                .description("Desc long enough to pass validation...")
                .location("Loc")
                .experienceRequired(2)
                .companyName("Comp")
                .expiryDays(5)
                .build();

        JobEntity persisted = JobEntity.builder().jobId(123L).title(dto.getTitle()).companyName(dto.getCompanyName()).build();
        when(jobRepo.save(any(JobEntity.class))).thenReturn(persisted);

        var res = service.createJob(dto);
        assertThat(res.getJobId()).isEqualTo(123L);
    }

    @Test
    void updateJob_notFound_throws() {
        when(jobRepo.findById(99L)).thenReturn(Optional.empty());
        Throwable t = catchThrowable(() -> service.updateJob(99L, new JobCommandDto()));
        assertThat(t).isInstanceOf(JobNotFoundException.class);
    }

    @Test
    void deleteJob_notFound_throws() {
        when(jobRepo.findById(55L)).thenReturn(Optional.empty());
        Throwable t = catchThrowable(() -> service.deleteJob(55L));
        assertThat(t).isInstanceOf(JobNotFoundException.class);
    }

    @Test
    void updateJob_updatesFieldsAndSaves() {
        JobEntity existing = JobEntity.builder()
                .jobId(200L)
                .title("Old")
                .description("Old")
                .location("Old")
                .experienceRequired(1)
                .companyName("C")
                .postedDate(null)
                .build();
        when(jobRepo.findById(200L)).thenReturn(Optional.of(existing));
        when(jobRepo.save(any(JobEntity.class))).thenAnswer(i -> i.getArgument(0));

        JobCommandDto payload = JobCommandDto.builder()
                .title("New Title")
                .experienceRequired(5)
                .build();

        var dto = service.updateJob(200L, payload);
        assertThat(dto.getTitle()).isEqualTo("New Title");
        assertThat(dto.getExperienceRequired()).isEqualTo(5);
    }
}