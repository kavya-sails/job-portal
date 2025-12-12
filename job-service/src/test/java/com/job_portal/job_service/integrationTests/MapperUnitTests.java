package com.job_portal.job_service.integrationTests;

import com.job_portal.job_service.dto.query.ApplicationHistoryQueryDto;
import com.job_portal.job_service.dto.query.JobDetailsQueryDto;
import com.job_portal.job_service.entity.ApplicationEntity;
import com.job_portal.job_service.entity.ApplicationStatus;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.mapper.ApplicationCommandMapper;
import com.job_portal.job_service.mapper.JobApplicationMapper;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

public class MapperUnitTests {

    @Test
    public void applicationMapper_convertsProperly() {
        JobEntity job = JobEntity.builder().jobId(11L).title("M").companyName("C").build();
        ApplicationEntity a = ApplicationEntity.builder()
                .applicationId(10L)
                .job(job)
                .userId(2L)
                .companyName("C")
                .appliedDate(Instant.now())
                .status(ApplicationStatus.PENDING)
                .build();
        ApplicationHistoryQueryDto dto = ApplicationCommandMapper.toApplicationHistory(a);
        assertThat(dto.getApplicationId()).isEqualTo(10L);
        assertThat(dto.getJobId()).isEqualTo(11L);
        assertThat(dto.getStatus()).isEqualTo(ApplicationStatus.PENDING);
    }

    @Test
    public void jobMapper_convertsProperly() {
        JobEntity e = JobEntity.builder()
                .jobId(3L)
                .title("T")
                .description("D")
                .location("Loc")
                .experienceRequired(4)
                .companyName("Co")
                .packageOffered("P")
                .skills("S")
                .education("E")
                .postedDate(Instant.now())
                .expiresAt(Instant.now().plusSeconds(1000))
                .build();
        JobDetailsQueryDto dto = JobApplicationMapper.toDetails(e);
        assertThat(dto.getJobId()).isEqualTo(3L);
        assertThat(dto.getTitle()).isEqualTo("T");
        assertThat(dto.getCompanyName()).isEqualTo("Co");
    }
}