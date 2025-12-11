package com.jobportal.user_service.controller;

import com.jobportal.user_service.dto.UserProfilePartialUpdateDto;
import com.jobportal.user_service.dto.UserProfileRequestDto;
import com.jobportal.user_service.dto.UserProfileResponseDto;
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
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    // CREATE: header ID is used as profile ID (FK to AuthUser.userId)
    @PostMapping("/create")
    public ResponseEntity<UserProfileResponseDto> createUserProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserProfileRequestDto dto
    ) {
        UserProfileResponseDto response = userProfileService.createUserProfile(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // READ: service checks pathId vs headerId
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponseDto> getById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId
    ) {
        UserProfileResponseDto response = userProfileService.getUserProfileById(id, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserProfileResponseDto>> getAll() {
        List<UserProfileResponseDto> list = userProfileService.getAllUserProfiles();
        return ResponseEntity.ok(list);
    }

    //service checks pathId vs headerId
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId
    ) {
        userProfileService.deleteUserProfileById(id, userId);
        return ResponseEntity.noContent().build();
    }

    // service checks pathId vs headerId
    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponseDto> update(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserProfileRequestDto dto
    ) {
        UserProfileResponseDto response = userProfileService.updateUserProfile(id, userId, dto);
        return ResponseEntity.ok(response);
    }

    // PARTIAL UPDATE: service checks pathId vs headerId
    @PatchMapping("/{id}")
    public ResponseEntity<UserProfileResponseDto> patch(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserProfilePartialUpdateDto dto
    ) {
        UserProfileResponseDto response = userProfileService.partialUpdateUserProfile(id, userId, dto);
        return ResponseEntity.ok(response);
    }

    public void applicationHistory(){}

}
