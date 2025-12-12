package com.job_portal.job_service.exception;

public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException(Long applicationId) {
        super("Application not found with ID: " + applicationId);
    }
}
