package com.jobportal.user_service.exception;

public class InvalidJobStatusException extends RuntimeException{
    public InvalidJobStatusException(String message) {
        super(message);
    }
}
