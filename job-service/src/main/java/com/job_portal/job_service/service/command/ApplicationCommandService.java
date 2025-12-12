package com.job_portal.job_service.service.command;

import com.job_portal.job_service.client.UserClient;
import com.job_portal.job_service.dto.command.ApplicationStatusResponseDto;
import com.job_portal.job_service.dto.event.ApplicationStatusEvent;
import com.job_portal.job_service.dto.query.ApplicationHistoryQueryDto;
import com.job_portal.job_service.entity.ApplicationEntity;
import com.job_portal.job_service.entity.ApplicationStatus;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.exception.*;
import com.job_portal.job_service.mapper.ApplicationCommandMapper;
import com.job_portal.job_service.publisher.ApplicationStatusPublisher;
import com.job_portal.job_service.repository.command.ApplicationCommandRepository;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ApplicationCommandService {
    private final ApplicationCommandRepository applicationCommandRepository;
    private final JobQueryRepository jobQueryRepository;
    private final ApplicationStatusPublisher statusPublisher;
    private final UserClient userClient;

    @Transactional
    public ApplicationHistoryQueryDto apply(Long userId, Long jobId) {
        try {
            userClient.checkUserExists(userId);
        } catch (UserNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("user-service unavailable", ex);
        }
        JobEntity job = jobQueryRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId));

        applicationCommandRepository.findByJobJobIdAndUserId(jobId, userId)
                .ifPresent(a -> {
                    throw new ApplicationConflictException(jobId, userId);
                });

        ApplicationEntity app = ApplicationEntity.builder()
                .job(job)
                .userId(userId)
                .companyName(job.getCompanyName())
                .appliedDate(Instant.now())
                .status(ApplicationStatus.PENDING)
                .build();
        ApplicationEntity saved = applicationCommandRepository.save(app);
        return ApplicationCommandMapper.toApplicationHistory(saved);
    }

    @Transactional
    public ApplicationStatusResponseDto updateStatus(Long applicationId, ApplicationStatus newStatus) {
        ApplicationEntity app = applicationCommandRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));

        app.setStatus(newStatus);
        ApplicationEntity updated = applicationCommandRepository.save(app);
        ApplicationStatusEvent event = ApplicationStatusEvent.builder()
                .applicationId(updated.getApplicationId())
                .userId(updated.getUserId())
                .jobId(updated.getJob().getJobId())
                .jobTitle(updated.getJob().getTitle())
                .status(updated.getStatus())
                .build();

        try {
            statusPublisher.publish(event);
        } catch (Exception e) {
            throw new MessagePublishException(e.getMessage());
        }

        return ApplicationCommandMapper.toStatusResponse(updated);
    }
}