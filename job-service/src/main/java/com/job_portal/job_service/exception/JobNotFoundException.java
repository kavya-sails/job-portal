package com.job_portal.job_service.exception;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(Long jobId) {
        super("Job not found with ID: " + jobId);
    }
}

