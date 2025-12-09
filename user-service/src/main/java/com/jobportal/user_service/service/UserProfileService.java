package com.jobportal.user_service.service;

import com.jobportal.user_service.dto.UserPartialUpdateDto;
import com.jobportal.user_service.dto.UserRequestDto;
import com.jobportal.user_service.dto.UserResponseDto;
import com.jobportal.user_service.entity.UserProfile;
import com.jobportal.user_service.exception.DataIntegrityViolationException; // your custom exception
import com.jobportal.user_service.exception.UserProfileNotFoundException;
import com.jobportal.user_service.mapper.UserMapper;
import com.jobportal.user_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserMapper userMapper;

    // CREATE USER (may cause 409 on duplicate email)
    public UserResponseDto createUserProfile(UserRequestDto dto) {
        try {
            UserProfile user = userMapper.toEntity(dto);
            UserProfile saved = userProfileRepository.save(user);
            return userMapper.toResponseDto(saved);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            //Catch Spring's DB exception and rethrow your custom one
            throw new DataIntegrityViolationException("Email already exists or violates a data constraint");
        }
    }

    public UserResponseDto getUserProfileById(Long id) {
        UserProfile user = userProfileRepository.findById(id)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User not found with id: " + id)
                );
        return userMapper.toResponseDto(user);
    }

    public List<UserResponseDto> getAllUserProfiles() {
        List<UserProfile> users = userProfileRepository.findAll();
        return userMapper.toResponseDTOList(users);
    }

    public void deleteUserProfileById(Long id) {
        UserProfile user = userProfileRepository.findById(id)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User not found with id: " + id)
                );

        user.setIsActive(false);   // soft delete
        userProfileRepository.save(user);
    }

    // FULL UPDATE (can also cause duplicate email)
    public UserResponseDto updateUserProfile(Long id, UserRequestDto dto) {
        UserProfile user = userProfileRepository.findById(id)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User not found with id: " + id)
                );

        try {
            userMapper.updateEntityFromDto(dto, user);
            UserProfile updatedUser = userProfileRepository.save(user);
            return userMapper.toResponseDto(updatedUser);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Email already exists or violates a data constraint");
        }
    }

    // PARTIAL UPDATE (also can cause duplicate email)
    public UserResponseDto partialUpdateUserProfile(Long id, UserPartialUpdateDto dto) {
        UserProfile user = userProfileRepository.findById(id)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User not found with id: " + id)
                );

        try {
            userMapper.patchEntityFromDto(dto, user);
            UserProfile updatedUser = userProfileRepository.save(user);
            return userMapper.toResponseDto(updatedUser);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Email already exists or violates a data constraint");
        }
    }

}
