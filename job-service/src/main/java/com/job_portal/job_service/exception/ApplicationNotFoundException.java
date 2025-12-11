package com.job_portal.job_service.exception;

import com.job_portal.job_service.exception.Enum.ErrorCode;

public class ApplicationNotFoundException extends BaseAppException {
    public ApplicationNotFoundException(Long applicationId) {
        super(ErrorCode.BAD_REQUEST,
                "Application not found: " + applicationId,
                "No application exists with id = " + applicationId);
    }
}

