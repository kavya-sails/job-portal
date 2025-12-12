package com.job_portal.job_service.service.query;

import com.job_portal.job_service.dto.query.JobDetailsQueryDto;
import com.job_portal.job_service.dto.query.JobSearchCriteria;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.exception.JobNotFoundException;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentMatchers;

@ExtendWith(MockitoExtension.class)
class JobQueryServiceTest {

    @Mock
    private JobQueryRepository jobRepo;

    @InjectMocks
    private JobQueryService jobQueryService;

    @Test
    void getAllJobs_returnsMappedList() {
        JobEntity e1 = JobEntity.builder().jobId(1L).title("A").build();
        JobEntity e2 = JobEntity.builder().jobId(2L).title("B").build();
        when(jobRepo.findAll()).thenReturn(List.of(e1, e2));

        List<JobDetailsQueryDto> result = jobQueryService.getAllJobs();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(JobDetailsQueryDto::getJobId).containsExactlyInAnyOrder(1L, 2L);
        verify(jobRepo).findAll();
    }

    @Test
    void getJobDetails_found_returnsDto() {
        JobEntity e = JobEntity.builder().jobId(10L).title("Dev").build();
        when(jobRepo.findById(10L)).thenReturn(Optional.of(e));

        JobDetailsQueryDto dto = jobQueryService.getJobDetails(10L);

        assertThat(dto).isNotNull();
        assertThat(dto.getJobId()).isEqualTo(10L);
        assertThat(dto.getTitle()).isEqualTo("Dev");
        verify(jobRepo).findById(10L);
    }

    @Test
    void getJobDetails_notFound_throws() {
        when(jobRepo.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> jobQueryService.getJobDetails(999L)).isInstanceOf(JobNotFoundException.class);
        verify(jobRepo).findById(999L);
    }

    @Test
    void searchJobs_mapsPageIntoDtoPage() {
        JobEntity e1 = JobEntity.builder().jobId(21L).title("P").build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<JobEntity> page = new PageImpl<>(List.of(e1), pageable, 1);

        when(jobRepo.findAll(ArgumentMatchers.<Specification<JobEntity>>any(), eq(pageable))).thenReturn(page);

        JobSearchCriteria criteria = new JobSearchCriteria();

        Page<JobDetailsQueryDto> result = jobQueryService.searchJobs(criteria, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getJobId()).isEqualTo(21L);
        verify(jobRepo).findAll(ArgumentMatchers.<Specification<JobEntity>>any(), eq(pageable));
    }
}