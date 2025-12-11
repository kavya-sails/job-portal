package com.job_portal.job_service.exception;

import com.job_portal.job_service.exception.Enum.ErrorCode;

//public class JobNotFoundException  extends RuntimeException {
//    public JobNotFoundException(Long jobId) {
//        super("Job not found: " + jobId);
//    }
//}
public class JobNotFoundException extends BaseAppException {
    public JobNotFoundException(Long jobId) {
        super(ErrorCode.JOB_NOT_FOUND,
                "Job not found: " + jobId,
                "No job exists with id = " + jobId);
    }
}

