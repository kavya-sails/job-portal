package com.job_portal.job_service.exception;

public class MessagePublishException extends RuntimeException {
    public MessagePublishException(String message) {
        super("Failed to publish message: " + message);
    }
}