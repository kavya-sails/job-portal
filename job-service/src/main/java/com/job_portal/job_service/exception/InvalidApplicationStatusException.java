package com.job_portal.job_service.exception;

public class InvalidApplicationStatusException extends RuntimeException {
    public InvalidApplicationStatusException(String status) {
        super("Invalid status value: " + status + ". Allowed: PENDING, REVIEWED, SELECTED, REJECTED");
    }
}