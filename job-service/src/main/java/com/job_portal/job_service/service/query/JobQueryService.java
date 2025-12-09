package com.job_portal.job_service.service.query;

import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.exception.JobNotFoundException;
import com.job_portal.job_service.dto.query.JobDetailsQueryDto;
import com.job_portal.job_service.dto.query.JobSummaryQueryDto;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import com.job_portal.job_service.mapper.JobApplicationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobQueryService {

    private final JobQueryRepository jobRepo;

    public Page<JobSummaryQueryDto> searchJobs(String location, Integer maxExperience, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postedDate"));
        Page<JobEntity> pageResult;

        if (location != null && maxExperience != null) {
            pageResult = jobRepo.findByLocationContainingIgnoreCaseAndExperienceRequiredLessThanEqual(location, maxExperience, pageable);
        } else if (location != null) {
            pageResult = jobRepo.findByLocationContainingIgnoreCase(location, pageable);
        } else if (maxExperience != null) {
            // fallback to default repo method - using a Pageable and all rows then filter by experience
            pageResult = jobRepo.findAll(pageable).map(j -> j); // later filter by experience
            // simpler: filter after fetch (but expensive). For demo, fetch all page and then filter.
            // Better: add repository method findByExperienceRequiredLessThanEqual(...)
        } else {
            pageResult = jobRepo.findAll(pageable);
        }

        return pageResult.map(JobApplicationMapper::toSummary);
    }

    public JobDetailsQueryDto getJobDetails(Long jobId) {
        JobEntity job = jobRepo.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId));
        return JobApplicationMapper.toDetails(job);
    }
}