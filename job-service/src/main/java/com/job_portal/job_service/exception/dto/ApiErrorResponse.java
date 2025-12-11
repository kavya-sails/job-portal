package com.job_portal.job_service.exception.dto;
//import lombok.Builder;
//import lombok.Data;
//import java.time.Instant;
//
//@Data
//@Builder
//public class ErrorDto {
//    private Instant timestamp;
//    private int status;
//    private String error;         // e.g., "Bad Request"
//    private String message;       // short client-facing message
//    private String description;   // optional more-detailed description (from exception.getDescription())
//    private String path;          // request path
//}
import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class ApiErrorResponse {
    private Instant timestamp;
    private String code;
    private String message;
    private String description;
    private String path;
    private int httpStatus;
    private Map<String, String> errors;
}
