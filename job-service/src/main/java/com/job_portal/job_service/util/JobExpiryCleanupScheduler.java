package com.job_portal.job_service.util;

import com.job_portal.job_service.repository.query.JobQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobExpiryCleanupScheduler {

    private final JobQueryRepository jobQueryRepository;

    // Run every day at 02:00 AM (server local) - CRON can be adjusted.
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupExpiredJobs() {
        Instant now = Instant.now();
        List jobsToDelete = jobQueryRepository.findByExpiresAtBefore(now);
        if (!jobsToDelete.isEmpty()) {
            log.info("Deleting {} expired jobs", jobsToDelete.size());
            jobQueryRepository.deleteAll(jobsToDelete);
        }
    }
}