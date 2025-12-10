package com.jobportal.user_service.controller;

import com.jobportal.user_service.dto.UserPartialUpdateDto;
import com.jobportal.user_service.dto.UserRequestDto;
import com.jobportal.user_service.dto.UserResponseDto;
import com.jobportal.user_service.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/profile")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserProfileController {

    private final UserProfileService userProfileService;

    // CREATE: header ID is used as profile ID (FK to AuthUser.userId)
    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> createUserProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserRequestDto dto
    ) {
        UserResponseDto response = userProfileService.createUserProfile(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // READ: service checks pathId vs headerId
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId
    ) {
        UserResponseDto response = userProfileService.getUserProfileById(id, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto>> getAll() {
        List<UserResponseDto> list = userProfileService.getAllUserProfiles();
        return ResponseEntity.ok(list);
    }

    // DELETE: service checks pathId vs headerId
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId
    ) {
        userProfileService.deleteUserProfileById(id, userId);
        return ResponseEntity.noContent().build();
    }

    // FULL UPDATE: service checks pathId vs headerId
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserRequestDto dto
    ) {
        UserResponseDto response = userProfileService.updateUserProfile(id, userId, dto);
        return ResponseEntity.ok(response);
    }

    // PARTIAL UPDATE: service checks pathId vs headerId
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> patch(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserPartialUpdateDto dto
    ) {
        UserResponseDto response = userProfileService.partialUpdateUserProfile(id, userId, dto);
        return ResponseEntity.ok(response);
    }
}
