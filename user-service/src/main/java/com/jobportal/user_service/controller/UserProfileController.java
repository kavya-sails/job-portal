package com.jobportal.user_service.controller;

import com.jobportal.user_service.dto.UserProfilePartialUpdateDto;
import com.jobportal.user_service.dto.UserProfileRequestDto;
import com.jobportal.user_service.dto.UserProfileResponseDto;
import com.jobportal.user_service.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users/profile")
@RequiredArgsConstructor
@Tag(
        name = "User Profile",
        description = "Operations for creating, updating, and fetching user profiles."
)
public class UserProfileController {
    private final UserProfileService userProfileService;

    @Operation(
            summary = "Create user profile",
            description = "Creates a new user profile using the provided user details and X-User-Id."
    )
    @PostMapping("/create")
    public ResponseEntity<UserProfileResponseDto> createUserProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserProfileRequestDto dto
    ) {
        UserProfileResponseDto response = userProfileService.createUserProfile(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Check if user profile exists",
            description = "Returns OK if the user profile exists in database."
    )
    @GetMapping("/apply/{userId}")
    public ResponseEntity<?> checkUser(@PathVariable Long userId) {
        userProfileService.checkUserExists(userId);
        return ResponseEntity.ok().build();
    }

    // fetches user with specific id
    @Operation(
            summary = "Get user profile by ID",
            description = "Returns user profile details for the given ID if accessible by logged-in user."
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponseDto> getById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId
    ) {
        UserProfileResponseDto response = userProfileService.getUserProfileById(id, userId);
        return ResponseEntity.ok(response);
    }

    // fetches all user profiles
    @Operation(
            summary = "Get all user profiles",
            description = "Returns list of all user profiles present in the system."
    )
    @GetMapping("/all")
    public ResponseEntity<List<UserProfileResponseDto>> getAll() {
        List<UserProfileResponseDto> list = userProfileService.getAllUserProfiles();
        return ResponseEntity.ok(list);
    }

    // Delete user profile
    @Operation(
            summary = "Delete user profile",
            description = "Soft deletes user profile by marking active = false."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId
    ) {
        userProfileService.deleteUserProfileById(id, userId);
        return ResponseEntity.noContent().build();
    }

    // update user profile
    @Operation(
            summary = "Update user profile",
            description = "Updates all user profile fields for the given ID."
    )
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
    @Operation(
            summary = "Partially update user profile",
            description = "Updates only selected fields in the user profile."
    )
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