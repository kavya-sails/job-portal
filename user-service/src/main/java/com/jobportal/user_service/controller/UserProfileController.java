package com.jobportal.user_service.controller;

import com.jobportal.user_service.dto.UserProfilePartialUpdateDto;
import com.jobportal.user_service.dto.UserProfileRequestDto;
import com.jobportal.user_service.dto.UserProfileResponseDto;
import com.jobportal.user_service.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users/profile")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;

    // Create user profile
    @PostMapping("/create")
    public ResponseEntity<UserProfileResponseDto> createUserProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserProfileRequestDto dto
    ) {
        UserProfileResponseDto response = userProfileService.createUserProfile(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // checks if user is present in db
    @GetMapping("/apply/{userId}")
    public ResponseEntity<?> checkUser(@PathVariable Long userId) {
        userProfileService.checkUserExists(userId);
        return ResponseEntity.ok().build();
    }

    // fetches user with specific id
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponseDto> getById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId
    ) {
        UserProfileResponseDto response = userProfileService.getUserProfileById(id, userId);
        return ResponseEntity.ok(response);
    }

    // fetches all user profiles
    @GetMapping("/all")
    public ResponseEntity<List<UserProfileResponseDto>> getAll() {
        List<UserProfileResponseDto> list = userProfileService.getAllUserProfiles();
        return ResponseEntity.ok(list);
    }

    // Delete user profile
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId
    ) {
        userProfileService.deleteUserProfileById(id, userId);
        return ResponseEntity.noContent().build();
    }

    // update user profile
    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponseDto> update(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserProfileRequestDto dto
    ) {
        UserProfileResponseDto response = userProfileService.updateUserProfile(id, userId, dto);
        return ResponseEntity.ok(response);
    }

    // Partial Update
    @PatchMapping("/{id}")
    public ResponseEntity<UserProfileResponseDto> patch(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserProfilePartialUpdateDto dto
    ) {
        UserProfileResponseDto response = userProfileService.partialUpdateUserProfile(id, userId, dto);
        return ResponseEntity.ok(response);
    }
}