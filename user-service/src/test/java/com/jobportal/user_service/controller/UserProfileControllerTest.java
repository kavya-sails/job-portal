package com.jobportal.user_service.controller;

import com.jobportal.user_service.dto.*;
import com.jobportal.user_service.enums.ExperienceLevel;
import com.jobportal.user_service.enums.JobRole;
import com.jobportal.user_service.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileControllerTest {

    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    private UserProfileController userProfileController;

    private UserProfileResponseDto responseDto;
    private UserProfileRequestDto requestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requestDto = UserProfileRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .dob(LocalDate.of(1995, 1, 1))
                .address("Hyderabad")
                .phone("9876543210")
                .skills("Java,Spring")
                .experience(3)
                .jobRole(JobRole.DESIGNER)
                .experienceLevel(ExperienceLevel.MID_LEVEL)
                .resumeUrl("https://resume.com/john")
                .portfolioUrl("https://portfolio.com/john")
                .linkedinUrl("https://linkedin.com/john")
                .education(EducationDto.builder()
                        .highestEducation(null)
                        .specialisation(null)
                        .institute("ABC College")
                        .passOutYear(2018)
                        .percentage(85.0)
                        .build())
                .build();

        responseDto = UserProfileResponseDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@gmail.com")
                .dob(LocalDate.of(1995, 1, 1))
                .phone("9876543210")
                .profileCompletionPercentage(90)
                .resumeUploadedAt(LocalDateTime.now())
                .build();
    }

    // CREATE PROFILE TEST
    @Test
    void testCreateUserProfile() {
        when(userProfileService.createUserProfile(requestDto, 10L))
                .thenReturn(responseDto);

        ResponseEntity<UserProfileResponseDto> response =
                userProfileController.createUserProfile(10L, requestDto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("John", response.getBody().getFirstName());
        verify(userProfileService, times(1)).createUserProfile(requestDto, 10L);
    }

    // CHECK USER EXISTS TEST
    @Test
    void testCheckUserExists() {
        doNothing().when(userProfileService).checkUserExists(5L);

        ResponseEntity<?> response = userProfileController.checkUser(5L);

        assertEquals(200, response.getStatusCodeValue());
        verify(userProfileService, times(1)).checkUserExists(5L);
    }

    // GET USER BY ID
    @Test
    void testGetUserProfileById() {
        when(userProfileService.getUserProfileById(1L, 10L))
                .thenReturn(responseDto);

        ResponseEntity<UserProfileResponseDto> response =
                userProfileController.getById(1L, 10L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("John", response.getBody().getFirstName());
        verify(userProfileService, times(1)).getUserProfileById(1L, 10L);
    }

    // GET ALL PROFILES
    @Test
    void testGetAllUserProfiles() {
        when(userProfileService.getAllUserProfiles())
                .thenReturn(List.of(responseDto));

        ResponseEntity<List<UserProfileResponseDto>> response =
                userProfileController.getAll();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(userProfileService, times(1)).getAllUserProfiles();
    }

    // DELETE USER PROFILE
    @Test
    void testDeleteUserProfile() {
        doNothing().when(userProfileService).deleteUserProfileById(1L, 10L);

        ResponseEntity<Void> response = userProfileController.delete(1L, 10L);

        assertEquals(204, response.getStatusCodeValue());
        verify(userProfileService, times(1)).deleteUserProfileById(1L, 10L);
    }


    // UPDATE USER PROFILE
    @Test
    void testUpdateUserProfile() {
        when(userProfileService.updateUserProfile(1L, 10L, requestDto))
                .thenReturn(responseDto);

        ResponseEntity<UserProfileResponseDto> response =
                userProfileController.update(1L, 10L, requestDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("John", response.getBody().getFirstName());
        verify(userProfileService, times(1))
                .updateUserProfile(1L, 10L, requestDto);
    }

    // PARTIAL UPDATE USER PROFILE
    @Test
    void testPartialUpdateUserProfile() {
        UserProfilePartialUpdateDto updateDto =
                UserProfilePartialUpdateDto.builder()
                        .firstName("Johnny")
                        .build();

        when(userProfileService.partialUpdateUserProfile(1L, 10L, updateDto))
                .thenReturn(responseDto);

        ResponseEntity<UserProfileResponseDto> response =
                userProfileController.patch(1L, 10L, updateDto);

        assertEquals(200, response.getStatusCodeValue());
        verify(userProfileService, times(1))
                .partialUpdateUserProfile(1L, 10L, updateDto);
    }
}
