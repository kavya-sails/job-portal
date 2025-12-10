package com.jobportal.user_service.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() { super("Forbidden"); }
    public ForbiddenException(String message) { super(message); }
}
