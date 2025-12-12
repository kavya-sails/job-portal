package com.job_portal.job_service.client;

import com.job_portal.job_service.exception.UserNotFoundException;
import feign.RequestInterceptor;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class FeignClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ErrorDecoder() {
            private final ErrorDecoder defaultDecoder = new Default();

            @Override
            public Exception decode(String methodKey, Response response) {
                if (response.status() == 404) {
                    return new UserNotFoundException("User doesn't exist");
                }
                return defaultDecoder.decode(methodKey, response);
            }
        };
    }
}