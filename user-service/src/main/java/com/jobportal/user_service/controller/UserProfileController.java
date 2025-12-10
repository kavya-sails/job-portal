package com.jobportal.user_service.controller;

import com.jobportal.user_service.dto.UserPartialUpdateDto;
import com.jobportal.user_service.dto.UserRequestDto;
import com.jobportal.user_service.dto.UserResponseDto;
import com.jobportal.user_service.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/profile")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserProfileController {

    private final UserProfileService userProfileService;

    // CREATE: header ID is used as profile ID (FK to AuthUser.userId)
    @PostMapping("/create")
    public UserResponseDto createUserProfile(@RequestHeader("X-User-Id") Long userId, @Valid @RequestBody UserRequestDto dto) {
        return userProfileService.createUserProfile(dto, userId);
    }

    // READ: service checks pathId vs headerId
    @GetMapping("/{id}")
    public UserResponseDto getById(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        return userProfileService.getUserProfileById(id, userId);
    }

    @GetMapping("/all")
    public List<UserResponseDto> getAll() {
        return userProfileService.getAllUserProfiles();
    }

    // DELETE: service checks pathId vs headerId
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId
    ) {
        userProfileService.deleteUserProfileById(id, userId);
    }

    // FULL UPDATE: service checks pathId vs headerId
    @PutMapping("/{id}")
    public UserResponseDto update(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserRequestDto dto
    ) {
        return userProfileService.updateUserProfile(id, userId, dto);
    }

    // PARTIAL UPDATE: service checks pathId vs headerId
    @PatchMapping("/{id}")
    public UserResponseDto patch(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserPartialUpdateDto dto
    ) {
        return userProfileService.partialUpdateUserProfile(id, userId, dto);
    }
}
