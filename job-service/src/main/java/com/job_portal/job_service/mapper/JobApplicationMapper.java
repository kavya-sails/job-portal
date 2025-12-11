//package com.job_portal.job_service.mapper;
//
//import com.job_portal.job_service.entity.ApplicationEntity;
//import com.job_portal.job_service.entity.JobEntity;
//import com.job_portal.job_service.dto.query.ApplicationHistoryQueryDto;
//import com.job_portal.job_service.dto.query.JobDetailsQueryDto;
//
//public class JobApplicationMapper {
//
//    public static JobDetailsQueryDto toDetails(JobEntity e){
//        if(e==null) return null;
//        return JobDetailsQueryDto.builder()
//                .jobId(e.getJobId())
//                .title(e.getTitle())
//                .description(e.getDescription())
//                .location(e.getLocation())
//                .experienceRequired(e.getExperienceRequired())
//                .postedDate(e.getPostedDate())
//                .build();
//    }
//
//    public static ApplicationHistoryQueryDto toApplicationHistory(ApplicationEntity a) {
//        if(a==null) return null;
//        return ApplicationHistoryQueryDto.builder()
//                .applicationId(a.getApplicationId())
//                .jobId(a.getJob().getJobId())
//                .jobTitle(a.getJob().getTitle())
//                .appliedDate(a.getAppliedDate())
//                .status(a.getStatus())
//                .build();
//    }
//}

package com.job_portal.job_service.mapper;

import com.job_portal.job_service.entity.ApplicationEntity;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.dto.query.ApplicationHistoryQueryDto;
import com.job_portal.job_service.dto.query.JobDetailsQueryDto;

public class JobApplicationMapper {

    public static JobDetailsQueryDto toDetails(JobEntity e){
        if(e==null) return null;
        return JobDetailsQueryDto.builder()
                .jobId(e.getJobId())
                .title(e.getTitle())
                .description(e.getDescription())
                .location(e.getLocation())
                .experienceRequired(e.getExperienceRequired())
                .companyName(e.getCompanyName())
                .packageOffered(e.getPackageOffered())
                .skills(e.getSkills())
                .education(e.getEducation())
                .postedDate(e.getPostedDate())
                .expiresAt(e.getExpiresAt())
                .build();
    }

    public static ApplicationHistoryQueryDto toApplicationHistory(ApplicationEntity a) {
        if(a==null) return null;
        return ApplicationHistoryQueryDto.builder()
                .applicationId(a.getApplicationId())
                .jobId(a.getJob().getJobId())
                .jobTitle(a.getJob().getTitle())
                .companyName(a.getCompanyName())   // ensure ApplicationHistoryQueryDto contains this
                .appliedDate(a.getAppliedDate())
                .status(a.getStatus())
                .build();
    }
}