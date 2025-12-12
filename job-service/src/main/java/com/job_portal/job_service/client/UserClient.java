package com.job_portal.job_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "USER-SERVICE",
        configuration = FeignClientConfig.class
)
public interface UserClient {
    @GetMapping("/api/users/profile/apply/{userId}")
    void checkUserExists(@PathVariable("userId") Long userId);
}
