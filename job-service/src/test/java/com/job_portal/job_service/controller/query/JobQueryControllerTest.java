package com.job_portal.job_service.controller.query;

import com.job_portal.job_service.dto.query.JobDetailsQueryDto;
import com.job_portal.job_service.dto.query.JobSearchCriteria;
import com.job_portal.job_service.service.query.JobQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobQueryControllerTest {

    @Mock
    private JobQueryService jobQueryService;

    @InjectMocks
    private JobQueryController controller;

    // NO @BeforeEach openMocks — MockitoExtension handles it

    @Test
    void listJobs_returnsMappedList() {
        JobDetailsQueryDto dto = JobDetailsQueryDto.builder().jobId(1L).title("T").build();
        when(jobQueryService.getAllJobs()).thenReturn(List.of(dto));

        var resp = controller.listJobs();
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).hasSize(1);
        assertThat(resp.getBody().get(0).getTitle()).isEqualTo("T");
    }

    @Test
    void getJob_returnsDto() {
        JobDetailsQueryDto dto = JobDetailsQueryDto.builder().jobId(5L).title("X").build();
        when(jobQueryService.getJobDetails(5L)).thenReturn(dto);

        var resp = controller.getJob(5L);
        assertThat(resp.getBody()).isEqualTo(dto);
        verify(jobQueryService).getJobDetails(5L);
    }

    @Test
    void searchJobs_returnsPage() {
        JobDetailsQueryDto d1 = JobDetailsQueryDto.builder().jobId(1L).title("a").build();
        Page<JobDetailsQueryDto> page = new PageImpl<>(List.of(d1), PageRequest.of(0, 10), 1);
        when(jobQueryService.searchJobs(any(JobSearchCriteria.class), any(Pageable.class))).thenReturn(page);

        var resp = controller.searchJobs("a", null, null, 0, 10, null);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody().getContent()).hasSize(1);
        verify(jobQueryService).searchJobs(any(JobSearchCriteria.class), any(Pageable.class));
    }
}