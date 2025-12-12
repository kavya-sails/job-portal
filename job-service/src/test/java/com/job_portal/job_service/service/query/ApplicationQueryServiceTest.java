package com.job_portal.job_service.service.query;

import com.job_portal.job_service.dto.query.ApplicationHistoryQueryDto;
import com.job_portal.job_service.entity.ApplicationEntity;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.repository.query.ApplicationQueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationQueryServiceTest {

    @Mock
    private ApplicationQueryRepository applicationQueryRepository;

    @InjectMocks
    private ApplicationQueryService applicationQueryService;

    @Test
    void getApplicationsByUser_returnsListMapped() {
        JobEntity job = JobEntity.builder()
                .jobId(10L)
                .title("Developer")
                .build();

        ApplicationEntity a = ApplicationEntity.builder()
                .applicationId(2L)
                .appliedDate(Instant.now())
                .job(job) // <-- Set job so mapper can read jobId
                .userId(5L)
                .build();
        when(applicationQueryRepository.findByUserIdOrderByAppliedDateDesc(5L)).thenReturn(List.of(a));

        List<ApplicationHistoryQueryDto> res = applicationQueryService.getApplicationsByUser(5L);

        assertThat(res).hasSize(1);
        assertThat(res.get(0).getApplicationId()).isEqualTo(2L);
        verify(applicationQueryRepository).findByUserIdOrderByAppliedDateDesc(5L);
    }

    @Test
    void getApplicationsByUser_emptyList_returnsEmpty() {
        when(applicationQueryRepository.findByUserIdOrderByAppliedDateDesc(7L)).thenReturn(Collections.emptyList());
        List<ApplicationHistoryQueryDto> res = applicationQueryService.getApplicationsByUser(7L);
        assertThat(res).isEmpty();
        verify(applicationQueryRepository).findByUserIdOrderByAppliedDateDesc(7L);
    }
}