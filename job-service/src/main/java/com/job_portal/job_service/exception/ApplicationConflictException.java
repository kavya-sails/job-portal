package com.job_portal.job_service.exception;

import com.job_portal.job_service.exception.Enum.ErrorCode;

//
//public class ApplicationConflictException extends RuntimeException{
//    public ApplicationConflictException(String message) {
//        super(message);
//    }
//}
public class ApplicationConflictException extends BaseAppException {
    public ApplicationConflictException(String message) {
        super(ErrorCode.APPLICATION_CONFLICT, message, null);
    }
}

