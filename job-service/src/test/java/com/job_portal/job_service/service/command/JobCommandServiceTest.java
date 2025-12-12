package com.job_portal.job_service.service.command;

import com.job_portal.job_service.dto.command.JobCommandDto;
import com.job_portal.job_service.dto.query.JobDetailsQueryDto;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.exception.JobNotFoundException;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobCommandServiceTest {

    @Mock
    private JobQueryRepository jobRepo;

    // class under test
    @InjectMocks
    private JobCommandService jobCommandService;

    @Test
    void createJob_shouldSaveAndReturnDetails() {
        // arrange: mock dto
        JobCommandDto dto = mock(JobCommandDto.class);
        when(dto.getTitle()).thenReturn("Developer");
        when(dto.getDescription()).thenReturn("desc");
        when(dto.getLocation()).thenReturn("Hyderabad");
        when(dto.getExperienceRequired()).thenReturn(3);
        when(dto.getCompanyName()).thenReturn("Acme");
        when(dto.getPackageOffered()).thenReturn("10 LPA");
        when(dto.getSkills()).thenReturn("Java,Spring");
        when(dto.getEducation()).thenReturn("BTech");
        when(dto.getPostedDate()).thenReturn(null);
        when(dto.getExpiryDays()).thenReturn(5);

        JobEntity saved = JobEntity.builder()
                .jobId(100L)
                .title("Developer")
                .description("desc")
                .location("Hyderabad")
                .experienceRequired(3)
                .companyName("Acme")
                .packageOffered("10 LPA")
                .skills("Java,Spring")
                .education("BTech")
                .postedDate(Instant.now())
                .expiresAt(Instant.now().plusSeconds(5 * 24 * 3600L))
                .build();

        when(jobRepo.save(any(JobEntity.class))).thenReturn(saved);

        // act
        JobDetailsQueryDto result = jobCommandService.createJob(dto);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getJobId()).isEqualTo(100L);
        assertThat(result.getTitle()).isEqualTo("Developer");
        verify(jobRepo).save(any(JobEntity.class));
    }

    @Test
    void updateJob_whenJobExists_shouldUpdateAndReturn() {
        Long jobId = 10L;
        JobCommandDto dto = mock(JobCommandDto.class);
        when(dto.getTitle()).thenReturn("New Title");
        when(dto.getDescription()).thenReturn(null);
        when(dto.getPostedDate()).thenReturn(null);
        when(dto.getExpiryDays()).thenReturn(2);

        JobEntity existing = JobEntity.builder()
                .jobId(jobId)
                .title("Old")
                .description("olddesc")
                .postedDate(Instant.now().minusSeconds(10))
                .build();

        when(jobRepo.findById(jobId)).thenReturn(Optional.of(existing));
        when(jobRepo.save(any(JobEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        JobDetailsQueryDto updated = jobCommandService.updateJob(jobId, dto);

        assertThat(updated).isNotNull();
        assertThat(updated.getTitle()).isEqualTo("New Title");
        assertThat(updated.getDescription()).isEqualTo("olddesc"); // unchanged
        verify(jobRepo).findById(jobId);
        verify(jobRepo).save(any(JobEntity.class));
    }

    @Test
    void updateJob_whenNotFound_shouldThrow() {
        Long absentId = 999L;
        JobCommandDto dto = mock(JobCommandDto.class);
        when(jobRepo.findById(absentId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> jobCommandService.updateJob(absentId, dto))
                .isInstanceOf(JobNotFoundException.class);
        verify(jobRepo).findById(absentId);
    }

    @Test
    void deleteJob_whenExists_shouldDelete() {
        Long id = 5L;
        JobEntity existing = JobEntity.builder().jobId(id).build();
        when(jobRepo.findById(id)).thenReturn(Optional.of(existing));

        jobCommandService.deleteJob(id);

        verify(jobRepo).findById(id);
        verify(jobRepo).delete(existing);
    }

    @Test
    void deleteJob_whenNotFound_shouldThrow() {
        Long id = 55L;

        when(jobRepo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobCommandService.deleteJob(id))
                .isInstanceOf(JobNotFoundException.class);

        verify(jobRepo).findById(id);
        verify(jobRepo, never()).delete(any(JobEntity.class)); // FIX
    }
}