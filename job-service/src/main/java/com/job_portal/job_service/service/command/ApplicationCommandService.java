package com.job_portal.job_service.service.command;

import com.job_portal.job_service.dto.command.ApplyJobCommand;
import com.job_portal.job_service.entity.ApplicationEntity;
import com.job_portal.job_service.entity.ApplicationStatus;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.exception.ApplicationConflictException;
import com.job_portal.job_service.exception.JobNotFoundException;
import com.job_portal.job_service.repository.command.ApplicationCommandRepository;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
@Service
@RequiredArgsConstructor
public class ApplicationCommandService {
    private final ApplicationCommandRepository applicationCommandRepository;
    private final JobQueryRepository jobQueryRepository;

    @Transactional
    public ApplicationEntity apply(@Valid ApplyJobCommand command) {
        // ensure job exists
        JobEntity job = jobQueryRepository.findById(command.getJobId())
                .orElseThrow(() -> new JobNotFoundException(command.getJobId()));

        // check conflict - same user applied before
        applicationCommandRepository.findByJobJobIdAndUserId(command.getJobId(), command.getUserId())
                .ifPresent(a -> { throw new ApplicationConflictException("User already applied to this job"); });

        ApplicationEntity app = ApplicationEntity.builder()
                .job(job)
                .userId(command.getUserId())
                .appliedDate(Instant.now())
                .status(ApplicationStatus.PENDING)
                .build();

        return applicationCommandRepository.save(app);
    }
}
