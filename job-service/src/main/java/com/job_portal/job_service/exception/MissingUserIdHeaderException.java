package com.job_portal.job_service.exception;

public class MissingUserIdHeaderException extends RuntimeException {
    public MissingUserIdHeaderException() {
        super("Missing or empty X-User-Id header");
    }
}