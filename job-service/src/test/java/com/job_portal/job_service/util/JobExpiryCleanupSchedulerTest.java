package com.job_portal.job_service.util;

import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import static org.mockito.Mockito.*;

class JobExpiryCleanupSchedulerTest {

    @Test
    void cleanupExpiredJobs_deletesExpired() {
        JobQueryRepository repo = mock(JobQueryRepository.class);
        JobExpiryCleanupScheduler sched = new JobExpiryCleanupScheduler(repo);

        JobEntity expired = JobEntity.builder()
                .jobId(1L)
                .expiresAt(Instant.now().minusSeconds(10))
                .build();

        when(repo.findByExpiresAtBefore(any(Instant.class)))
                .thenReturn(List.of(expired));

        sched.cleanupExpiredJobs();

        verify(repo).findByExpiresAtBefore(any(Instant.class));

        verify(repo).deleteAll(argThat(iter -> {
            if (!(iter instanceof List<?> list)) return false;
            return list.size() == 1 &&
                    ((JobEntity) list.get(0)).getJobId().equals(1L);
        }));
    }

    @Test
    void cleanupExpiredJobs_noOpsWhenNone() {
        JobQueryRepository repo = mock(JobQueryRepository.class);
        JobExpiryCleanupScheduler sched = new JobExpiryCleanupScheduler(repo);

        when(repo.findByExpiresAtBefore(any(Instant.class))).thenReturn(List.of());

        sched.cleanupExpiredJobs();

        verify(repo).findByExpiresAtBefore(any(Instant.class));
        verify(repo, never()).deleteAll(anyList());
    }
}