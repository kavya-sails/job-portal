package com.job_portal.job_service.service.query;

import com.job_portal.job_service.dto.query.ApplicationHistoryQueryDto;
import com.job_portal.job_service.entity.ApplicationEntity;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.repository.query.ApplicationQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ApplicationQueryServiceTest {

    @Mock
    private ApplicationQueryRepository repo;

    @InjectMocks
    private ApplicationQueryService service;

    @BeforeEach
    void setup() { MockitoAnnotations.openMocks(this); }

    @Test
    void getApplicationsByUser_returnsMappedList() {
        // create and attach a JobEntity so ApplicationCommandMapper won't NPE
        JobEntity job = JobEntity.builder()
                .jobId(2L)
                .title("Test Job")
                .companyName("TestCo")
                .location("Remote")
                .description("desc")
                .experienceRequired(1)
                .postedDate(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .skills("Java")
                .education("B.Tech")
                .packageOffered("5LPA")
                .build();

        ApplicationEntity e = ApplicationEntity.builder()
                .applicationId(10L)
                .userId("u1")
                .appliedDate(Instant.now())
                .job(job)                // <-- set job here
                .companyName(job.getCompanyName())
                .build();

        when(repo.findByUserIdOrderByAppliedDateDesc("u1")).thenReturn(List.of(e));

        List<ApplicationHistoryQueryDto> list = service.getApplicationsByUser("u1");
        assertThat(list).hasSize(1);
        assertThat(list.get(0)).isInstanceOf(ApplicationHistoryQueryDto.class);
        assertThat(list.get(0).getJobId()).isEqualTo(2L);
        verify(repo).findByUserIdOrderByAppliedDateDesc("u1");
    }
}