package com.jobportal.user_service.service;

import com.jobportal.user_service.dto.UserPartialUpdateDto;
import com.jobportal.user_service.dto.UserRequestDto;
import com.jobportal.user_service.dto.UserResponseDto;
import com.jobportal.user_service.entity.AuthUser;
import com.jobportal.user_service.entity.UserProfile;
import com.jobportal.user_service.exception.DataIntegrityViolationException;
import com.jobportal.user_service.exception.ForbiddenException;
import com.jobportal.user_service.exception.UserProfileNotFoundException;
import com.jobportal.user_service.mapper.UserMapper;
import com.jobportal.user_service.repository.AuthUserRepository;
import com.jobportal.user_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final AuthUserRepository authUserRepository;
    private final UserMapper userMapper;

    // CREATE USER with ID from header (FK to AuthUser)
    public UserResponseDto createUserProfile(UserRequestDto dto, Long headerUserId) {

        if (userProfileRepository.existsById(headerUserId)) {
            throw new DataIntegrityViolationException(
                    "User profile already exists for userId: " + headerUserId
            );
        }

        // Ensure AuthUser exists
        AuthUser authUser = authUserRepository.findById(headerUserId)
                .orElseThrow(() -> new UserProfileNotFoundException(
                        "Auth user not found with id: " + headerUserId
                ));

        try {
            UserProfile user = userMapper.toEntity(dto);
            user.setId(authUser.getUserId());   // primary key from header / auth user

            UserProfile saved = userProfileRepository.save(user);

            UserResponseDto response = userMapper.toResponseDto(saved);
            response.setEmail(authUser.getEmail()); // from AuthUser

            return response;
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException(
                    "Profile violates a data constraint"
            );
        }
    }

    // READ with access check in service
    public UserResponseDto getUserProfileById(Long pathId, Long headerUserId) {

        if (!pathId.equals(headerUserId)) {
            throw new ForbiddenException(
                    "You are not allowed to access this profile"
            );
        }

        UserProfile user = userProfileRepository.findById(pathId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User not found with id: " + pathId)
                );

        AuthUser authUser = authUserRepository.findById(pathId)
                .orElseThrow(() -> new UserProfileNotFoundException(
                        "Auth user not found with id: " + pathId
                ));

        UserResponseDto response = userMapper.toResponseDto(user);
        response.setEmail(authUser.getEmail());

        return response;
    }

    public List<UserResponseDto> getAllUserProfiles() {
        List<UserProfile> users = userProfileRepository.findAll();
        List<UserResponseDto> responseList = userMapper.toResponseDTOList(users);

        // Attach email for each (simple implementation; can optimize later)
        for (UserResponseDto resp : responseList) {
            authUserRepository.findById(resp.getId()).ifPresent(
                    authUser -> resp.setEmail(authUser.getEmail())
            );
        }

        return responseList;
    }

    // DELETE with access check
    public void deleteUserProfileById(Long pathId, Long headerUserId) {

        if (!pathId.equals(headerUserId)) {
            throw new ForbiddenException(
                    "You are not allowed to delete this profile"
            );
        }

        UserProfile user = userProfileRepository.findById(pathId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User not found with id: " + pathId)
                );

        user.setIsActive(false);   // soft delete
        userProfileRepository.save(user);
    }

    // FULL UPDATE with access check
    public UserResponseDto updateUserProfile(Long pathId,
                                             Long headerUserId,
                                             UserRequestDto dto) {

        if (!pathId.equals(headerUserId)) {
            throw new ForbiddenException(
                    "You are not allowed to update this profile"
            );
        }

        UserProfile user = userProfileRepository.findById(pathId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User not found with id: " + pathId)
                );

        AuthUser authUser = authUserRepository.findById(pathId)
                .orElseThrow(() -> new UserProfileNotFoundException(
                        "Auth user not found with id: " + pathId
                ));

        try {
            userMapper.updateEntityFromDto(dto, user);
            UserProfile updatedUser = userProfileRepository.save(user);

            UserResponseDto response = userMapper.toResponseDto(updatedUser);
            response.setEmail(authUser.getEmail());

            return response;
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException(
                    "Profile violates a data constraint"
            );
        }
    }

    // PARTIAL UPDATE with access check
    public UserResponseDto partialUpdateUserProfile(Long pathId,
                                                    Long headerUserId,
                                                    UserPartialUpdateDto dto) {

        if (!pathId.equals(headerUserId)) {
            throw new ForbiddenException(
                    "You are not allowed to update this profile"
            );
        }

        UserProfile user = userProfileRepository.findById(pathId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User not found with id: " + pathId)
                );

        AuthUser authUser = authUserRepository.findById(pathId)
                .orElseThrow(() -> new UserProfileNotFoundException(
                        "Auth user not found with id: " + pathId
                ));

        try {
            userMapper.patchEntityFromDto(dto, user);
            UserProfile updatedUser = userProfileRepository.save(user);

            UserResponseDto response = userMapper.toResponseDto(updatedUser);
            response.setEmail(authUser.getEmail());

            return response;
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException(
                    "Profile violates a data constraint"
            );
        }
    }
}
