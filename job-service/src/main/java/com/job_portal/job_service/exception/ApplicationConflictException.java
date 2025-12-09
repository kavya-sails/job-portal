package com.job_portal.job_service.exception;

public class ApplicationConflictException extends RuntimeException{
    public ApplicationConflictException(String message) {
        super(message);
    }

}
