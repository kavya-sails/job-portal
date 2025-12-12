package com.job_portal.job_service.exception;
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) { super("User doesn't exist"); }
}
