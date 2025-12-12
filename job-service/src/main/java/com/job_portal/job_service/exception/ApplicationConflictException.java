package com.job_portal.job_service.exception;

public class ApplicationConflictException extends RuntimeException {
    public ApplicationConflictException(Long jobId, Long userId) {
        super("User " + userId + " already applied to job " + jobId);
    }
}