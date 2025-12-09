package com.jobportal.user_service.controller;

import com.jobportal.user_service.dto.UserPartialUpdateDto;
import com.jobportal.user_service.dto.UserRequestDto;
import com.jobportal.user_service.dto.UserResponseDto;
import com.jobportal.user_service.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userProfileService;

    @GetMapping("/")
    String hello(){
        return "Hello";
    }

    @PostMapping("/create")
    UserResponseDto createUserProfile(@Valid @RequestBody UserRequestDto dto ){
        return userProfileService.createUserProfile(dto);
    }

    @GetMapping("/{id}")
    public UserResponseDto getById(@PathVariable Long id) {
        return userProfileService.getUserProfileById(id);
    }

    @GetMapping("/all")
    public List<UserResponseDto> getAll() {
        return userProfileService.getAllUserProfiles();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userProfileService.deleteUserProfileById(id);
    }

    @PutMapping("/{id}")
    public UserResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto dto) {
        return userProfileService.updateUserProfile(id, dto);
    }

    @PatchMapping("/{id}")
    public UserResponseDto patch(
            @PathVariable Long id,
            @Valid @RequestBody UserPartialUpdateDto dto) {
        return userProfileService.partialUpdateUserProfile(id, dto);
    }


}
