package com.job_portal.job_service.exception.Enum;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    JOB_NOT_FOUND("JOB_NOT_FOUND", HttpStatus.NOT_FOUND),
    APPLICATION_CONFLICT("APPLICATION_CONFLICT", HttpStatus.CONFLICT),
    VALIDATION_FAILED("VALIDATION_FAILED", HttpStatus.BAD_REQUEST),
    BAD_REQUEST("BAD_REQUEST", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("UNAUTHORIZED", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("FORBIDDEN", HttpStatus.FORBIDDEN),
    INTERNAL_ERROR("INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final HttpStatus httpStatus;

    ErrorCode(String code, HttpStatus httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }

}

