package com.job_portal.job_service.exception;

import com.job_portal.job_service.exception.Enum.ErrorCode;
import lombok.Getter;

@Getter
public abstract class BaseAppException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String description; // extra context

    protected BaseAppException(ErrorCode errorCode, String message, String description) {
        super(message);
        this.errorCode = errorCode;
        this.description = description;
    }
}
