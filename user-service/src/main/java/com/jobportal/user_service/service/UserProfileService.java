package com.jobportal.user_service.service;

import com.jobportal.user_service.dto.UserPartialUpdateDto;
import com.jobportal.user_service.dto.UserRequestDto;
import com.jobportal.user_service.dto.UserResponseDto;
import com.jobportal.user_service.entity.AuthUser;
import com.jobportal.user_service.entity.UserEducation;
import com.jobportal.user_service.entity.UserProfile;
import com.jobportal.user_service.exception.DataIntegrityViolationException;
import com.jobportal.user_service.exception.ForbiddenException;
import com.jobportal.user_service.exception.UserProfileNotFoundException;
import com.jobportal.user_service.mapper.UserMapper;
import com.jobportal.user_service.repository.UserProfileRepository;
import com.jobportal.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;          // <- existing repo for AuthUser
    private final UserMapper userMapper;

    // ----------------------------------------------------
    // CREATE USER with ID from header (FK to AuthUser)
    // ----------------------------------------------------
    public UserResponseDto createUserProfile(UserRequestDto dto, Long headerUserId) {

        // Prevent duplicate profiles for same auth user
        if (userProfileRepository.existsById(headerUserId)) {
            throw new DataIntegrityViolationException(
                    "User profile already exists for userId: " + headerUserId
            );
        }

        // Ensure AuthUser exists
        AuthUser authUser = userRepository.findById(headerUserId)
                .orElseThrow(() -> new UserProfileNotFoundException(
                        "Auth user not found with id: " + headerUserId
                ));

        try {
            // Map request DTO to entity
            UserProfile userProfile = userMapper.toEntity(dto);
            // primary key is the same as auth_users.user_id
            userProfile.setId(authUser.getUserId());

            // calculate profile completion based on entity state
            recalculateProfileCompletion(userProfile);

            UserProfile saved = userProfileRepository.save(userProfile);

            // Map entity to response DTO and attach email from AuthUser
            UserResponseDto response = userMapper.toResponseDto(saved);
            response.setEmail(authUser.getEmail());

            return response;
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            // Wrap Spring exception in custom exception
            throw new DataIntegrityViolationException(
                    "Profile violates a data constraint"
            );
        }
    }

    // ----------------------------------------------------
    // READ with access check in service
    // ----------------------------------------------------
    public UserResponseDto getUserProfileById(Long pathId, Long headerUserId) {

        if (!pathId.equals(headerUserId)) {
            throw new ForbiddenException(
                    "You are not allowed to access this profile"
            );
        }

        UserProfile userProfile = userProfileRepository.findById(pathId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User not found with id: " + pathId)
                );

        AuthUser authUser = userRepository.findById(pathId)
                .orElseThrow(() -> new UserProfileNotFoundException(
                        "Auth user not found with id: " + pathId
                ));

        UserResponseDto response = userMapper.toResponseDto(userProfile);
        response.setEmail(authUser.getEmail());

        return response;
    }

    // ----------------------------------------------------
    // READ ALL (no access check – typically ADMIN use)
    // ----------------------------------------------------
    public List<UserResponseDto> getAllUserProfiles() {
        List<UserProfile> users = userProfileRepository.findAll();
        List<UserResponseDto> responseList = userMapper.toResponseDTOList(users);

        // Attach email for each profile from AuthUser
        for (UserResponseDto resp : responseList) {
            userRepository.findById(resp.getId()).ifPresent(
                    authUser -> resp.setEmail(authUser.getEmail())
            );
        }

        return responseList;
    }

    // ----------------------------------------------------
    // DELETE with access check
    // (hard delete, since isActive has been removed)
    // ----------------------------------------------------
    public void deleteUserProfileById(Long pathId, Long headerUserId) {

        if (!pathId.equals(headerUserId)) {
            throw new ForbiddenException(
                    "You are not allowed to delete this profile"
            );
        }

        UserProfile userProfile = userProfileRepository.findById(pathId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User not found with id: " + pathId)
                );

        // Hard delete (no soft delete flag anymore)
        userProfileRepository.delete(userProfile);
    }

    // ----------------------------------------------------
    // FULL UPDATE with access check (PUT)
    // ----------------------------------------------------
    public UserResponseDto updateUserProfile(Long pathId,
                                             Long headerUserId,
                                             UserRequestDto dto) {

        if (!pathId.equals(headerUserId)) {
            throw new ForbiddenException(
                    "You are not allowed to update this profile"
            );
        }

        UserProfile userProfile = userProfileRepository.findById(pathId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User not found with id: " + pathId)
                );

        AuthUser authUser = userRepository.findById(pathId)
                .orElseThrow(() -> new UserProfileNotFoundException(
                        "Auth user not found with id: " + pathId
                ));

        try {
            // Update existing entity from DTO (MapStruct)
            userMapper.updateEntityFromDto(dto, userProfile);

            // Recalculate profile completion from updated entity
            recalculateProfileCompletion(userProfile);

            UserProfile updatedUser = userProfileRepository.save(userProfile);

            UserResponseDto response = userMapper.toResponseDto(updatedUser);
            response.setEmail(authUser.getEmail());

            return response;
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException(
                    "Profile violates a data constraint"
            );
        }
    }

    // ----------------------------------------------------
    // PARTIAL UPDATE with access check (PATCH)
    // ----------------------------------------------------
    public UserResponseDto partialUpdateUserProfile(Long pathId,
                                                    Long headerUserId,
                                                    UserPartialUpdateDto dto) {

        if (!pathId.equals(headerUserId)) {
            throw new ForbiddenException(
                    "You are not allowed to update this profile"
            );
        }

        UserProfile userProfile = userProfileRepository.findById(pathId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User not found with id: " + pathId)
                );

        AuthUser authUser = userRepository.findById(pathId)
                .orElseThrow(() -> new UserProfileNotFoundException(
                        "Auth user not found with id: " + pathId
                ));

        try {
            // Patch existing entity from DTO (MapStruct IGNORE nulls)
            userMapper.patchEntityFromDto(dto, userProfile);

            // Recalculate profile completion from patched entity
            recalculateProfileCompletion(userProfile);

            UserProfile updatedUser = userProfileRepository.save(userProfile);

            UserResponseDto response = userMapper.toResponseDto(updatedUser);
            response.setEmail(authUser.getEmail());

            return response;
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException(
                    "Profile violates a data constraint"
            );
        }
    }

    // ----------------------------------------------------
    // PROFILE COMPLETION CALCULATION
    // ----------------------------------------------------

    /**
     * Calculate profileCompletionPercentage based on non-empty fields
     * of UserProfile and UserEducation. System fields like id, createdAt,
     * updatedAt, resumeUploadedAt are ignored.
     */
    private void recalculateProfileCompletion(UserProfile profile) {
        int total = 0;
        int filled = 0;

        // --- UserProfile fields (user-facing) ---
        total++;
        if (isNotBlank(profile.getFirstName())) filled++;

        total++;
        if (isNotBlank(profile.getLastName())) filled++;

        total++;
        if (profile.getDob() != null) filled++;

        total++;
        if (isNotBlank(profile.getAddress())) filled++;

        total++;
        if (isNotBlank(profile.getPhone())) filled++;

        total++;
        if (isNotBlank(profile.getSkills())) filled++;

        total++;
        if (profile.getExperience() != null) filled++;

        total++;
        if (profile.getJobRole() != null) filled++;

        total++;
        if (profile.getExperienceLevel() != null) filled++;

        total++;
        if (isNotBlank(profile.getResumeUrl())) filled++;

        total++;
        if (isNotBlank(profile.getPortfolioUrl())) filled++;

        total++;
        if (isNotBlank(profile.getLinkedinUrl())) filled++;

        // --- Education fields ---
        UserEducation edu = profile.getEducation();
        if (edu != null) {
            total++;
            if (edu.getHighestEducation() != null) filled++;

            total++;
            if (edu.getSpecialisation() != null) filled++;

            total++;
            if (isNotBlank(edu.getInstitute())) filled++;

            total++;
            if (isNotBlank(edu.getLocation())) filled++;

            total++;
            if (edu.getPassOutYear() != null) filled++;

            total++;
            if (edu.getPercentage() != null) filled++;
        }

        int percentage = (total == 0)
                ? 0
                : (int) Math.round((filled * 100.0) / total);

        profile.setProfileCompletionPercentage(percentage);
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
