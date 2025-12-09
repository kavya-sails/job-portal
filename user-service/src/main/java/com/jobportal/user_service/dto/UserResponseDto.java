package com.jobportal.user_service.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private Long id;

    private String firstName;
    private String lastName;
    private String email;

    private LocalDate dob;
    private String address;
    private String phone;

    private String highestEducation;
    private String skills;
    private Integer experience;

    private String resumeUrl;
    private LocalDateTime resumeUploadedAt;

}
