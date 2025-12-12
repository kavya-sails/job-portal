package com.job_portal.job_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages="com.job_service.client" )
public class JobServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(JobServiceApplication.class, args);
	}

}
