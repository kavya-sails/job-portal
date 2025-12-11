package com.job_portal.job_service.exception;

import com.job_portal.job_service.exception.Enum.ErrorCode;

public class BadRequestException extends BaseAppException {
    public BadRequestException(String message) {
        super(ErrorCode.BAD_REQUEST, message, null);
    }
}


