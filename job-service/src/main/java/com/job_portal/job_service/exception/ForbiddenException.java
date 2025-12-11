package com.job_portal.job_service.exception;

import com.job_portal.job_service.exception.Enum.ErrorCode;

public class ForbiddenException extends BaseAppException {
    public ForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN, message, null);
    }
}
