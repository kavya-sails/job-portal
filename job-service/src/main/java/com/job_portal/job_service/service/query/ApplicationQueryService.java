package com.job_portal.job_service.service.query;

import com.job_portal.job_service.dto.query.ApplicationHistoryQueryDto;
import com.job_portal.job_service.mapper.ApplicationCommandMapper;
import com.job_portal.job_service.mapper.JobApplicationMapper;
import com.job_portal.job_service.repository.query.ApplicationQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationQueryService {

    private final ApplicationQueryRepository applicationQueryRepository;

    public List<ApplicationHistoryQueryDto> getApplicationsByUser(String userId) {
        return applicationQueryRepository.findByUserIdOrderByAppliedDateDesc(userId)
                .stream()
                .map(ApplicationCommandMapper::toApplicationHistory)
                .collect(Collectors.toList());
    }
}