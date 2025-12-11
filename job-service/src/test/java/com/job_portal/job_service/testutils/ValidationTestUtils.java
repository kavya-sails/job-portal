package com.job_portal.job_service.testutils;

import jakarta.validation.ConstraintViolation;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Set;

public class ValidationTestUtils {

    /** Build a MethodArgumentNotValidException for a target object from constraint violations. */
    public static MethodArgumentNotValidException buildMethodArgNotValidException(Object target, Set<ConstraintViolation<Object>> violations) {
        BindingResult bindingResult = new BeanPropertyBindingResult(target, target.getClass().getSimpleName());
        // Put violation messages into BindingResult field errors
        for (ConstraintViolation<Object> v : violations) {
            String field = v.getPropertyPath().toString();
            String message = v.getMessage();
            bindingResult.rejectValue(field, "invalid", message);
        }
        return new MethodArgumentNotValidException(null, bindingResult);
    }
}