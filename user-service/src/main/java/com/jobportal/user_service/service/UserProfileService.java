package com.jobportal.user_service.service;

import com.jobportal.user_service.dto.UserProfilePartialUpdateDto;
import com.jobportal.user_service.dto.UserProfileRequestDto;
import com.jobportal.user_service.dto.UserProfileResponseDto;
import com.jobportal.user_service.entity.UserCredential;
import com.jobportal.user_service.entity.UserEducation;
import com.jobportal.user_service.entity.UserProfile;
import com.jobportal.user_service.exception.DataIntegrityViolationException;
import com.jobportal.user_service.exception.ForbiddenException;
import com.jobportal.user_service.exception.UserNotFound;
import com.jobportal.user_service.exception.UserProfileNotFoundException;
import com.jobportal.user_service.mapper.UserEducationMapper;
import com.jobportal.user_service.mapper.UserProfileMapper;
import com.jobportal.user_service.repository.UserCredentialRepository;
import com.jobportal.user_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final UserProfileMapper userProfileMapper;
    private final UserEducationMapper userEducationMapper;

    @Transactional
    public UserProfileResponseDto createUserProfile(UserProfileRequestDto dto, Long headerUserId) {
        // Prevent duplicate profiles for same auth user
        if (userProfileRepository.existsById(headerUserId)) {
            throw new DataIntegrityViolationException("User profile already exists for userId: " + headerUserId);
        }
        UserCredential authUser = fetchAuthUserOrThrow(headerUserId);
        UserProfile profile = userProfileMapper.toEntity(dto);
        profile.setId(authUser.getUserId());
        // handle education if present in DTO
        if (dto.getEducation() != null) {
            UserEducation edu = userEducationMapper.toEntity(dto.getEducation());
            edu.setUserProfile(profile);
            profile.setEducation(edu);
        }
        recalculateProfileCompletion(profile);
        UserProfile saved = userProfileRepository.save(profile);
        return buildResponseWithEmail(saved, authUser.getEmail());
    }

    public UserProfileResponseDto getUserProfileById(Long pathId, Long headerUserId) {
        ensureOwner(pathId, headerUserId);
        UserProfile userProfile = userProfileRepository.findById(pathId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found with id: " + pathId));

        String email = fetchAuthUserEmail(headerUserId);
        return buildResponseWithEmail(userProfile, email);
    }

    public List<UserProfileResponseDto> getAllUserProfiles() {
        List<UserProfile> users = userProfileRepository.findAll();
        return users.stream()
                .map(up -> buildResponseWithEmail(up, fetchAuthUserEmail(up.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUserProfileById(Long pathId, Long headerUserId) {
        ensureOwner(pathId, headerUserId);
        UserProfile userProfile = userProfileRepository.findById(pathId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found with id: " + pathId));
        userProfileRepository.delete(userProfile);
    }

    @Transactional
    public UserProfileResponseDto updateUserProfile(Long pathId, Long headerUserId, UserProfileRequestDto dto) {
        ensureOwner(pathId, headerUserId);
        UserProfile userProfile = userProfileRepository.findById(pathId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found with id: " + pathId));

        String oldResumeUrl = userProfile.getResumeUrl();
        UserCredential authUser = fetchAuthUserOrThrow(pathId);

        // apply changes (scalars + education)
        applyProfileChangesFromRequest(dto, userProfile);

        updateResumeTimestampIfChanged(oldResumeUrl, userProfile.getResumeUrl(), userProfile);
        recalculateProfileCompletion(userProfile);

        UserProfile updated = userProfileRepository.save(userProfile);
        return buildResponseWithEmail(updated, authUser.getEmail());
    }

    @Transactional
    public UserProfileResponseDto partialUpdateUserProfile(Long pathId, Long headerUserId, UserProfilePartialUpdateDto dto) {
        ensureOwner(pathId, headerUserId);
        UserProfile userProfile = userProfileRepository.findById(pathId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found with id: " + pathId));

        String oldResumeUrl = userProfile.getResumeUrl();
        UserCredential authUser = fetchAuthUserOrThrow(pathId);

        // patch scalars and education
        userProfileMapper.patchEntityFromDto(dto, userProfile);
        if (dto.getEducation() != null) {
            applyEducationPatch(dto, userProfile);
        }

        updateResumeTimestampIfChanged(oldResumeUrl, userProfile.getResumeUrl(), userProfile);
        recalculateProfileCompletion(userProfile);

        UserProfile updated = userProfileRepository.save(userProfile);
        return buildResponseWithEmail(updated, authUser.getEmail());
    }

    private void applyProfileChangesFromRequest(UserProfileRequestDto dto, UserProfile userProfile) {
        // map simple scalar fields (mapper ignores education per original)
        userProfileMapper.updateEntityFromDto(dto, userProfile);

        if (dto.getEducation() != null) {
            applyEducationUpdate(dto, userProfile);
        }
    }

    private void applyEducationUpdate(UserProfileRequestDto dto, UserProfile userProfile) {
        if (userProfile.getEducation() == null) {
            UserEducation edu = userEducationMapper.toEntity(dto.getEducation());
            edu.setUserProfile(userProfile);
            userProfile.setEducation(edu);
        } else {
            userEducationMapper.updateEntityFromDto(dto.getEducation(), userProfile.getEducation());
        }
    }

    private void applyEducationPatch(UserProfilePartialUpdateDto dto, UserProfile userProfile) {
        if (userProfile.getEducation() == null) {
            UserEducation edu = userEducationMapper.toEntity(dto.getEducation());
            edu.setUserProfile(userProfile);
            userProfile.setEducation(edu);
        } else {
            userEducationMapper.updateEntityFromDto(dto.getEducation(), userProfile.getEducation());
        }
    }

    private void ensureOwner(Long pathId, Long headerUserId) {
        if (!Objects.equals(pathId, headerUserId)) {
            throw new ForbiddenException("You are not allowed to access this profile");
        }
    }

    private UserCredential fetchAuthUserOrThrow(Long userId) {
        return userCredentialRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound("Auth user not found for id: " + userId));
    }

    private String fetchAuthUserEmail(Long userId) {
        return userCredentialRepository.findById(userId).map(UserCredential::getEmail).orElse(null);
    }

    private UserProfileResponseDto buildResponseWithEmail(UserProfile userProfile, String email) {
        UserProfileResponseDto response = userProfileMapper.toResponseDto(userProfile);
        response.setEmail(email);
        return response;
    }

    private void updateResumeTimestampIfChanged(String oldUrl, String newUrl, UserProfile profile) {
        if (newUrl != null && !newUrl.equals(oldUrl)) {
            profile.setResumeUploadedAt(LocalDateTime.now());
        }
    }

    private void recalculateProfileCompletion(UserProfile profile) {
        profile.setProfileCompletionPercentage(calculateProfileCompletionPercentage(profile));
    }

    private int calculateProfileCompletionPercentage(UserProfile profile) {
        Stream<Object> profileFields = Stream.of(
                profile.getFirstName(),
                profile.getLastName(),
                profile.getDob(),
                profile.getAddress(),
                profile.getPhone(),
                profile.getSkills(),
                profile.getExperience(),
                profile.getJobRole(),
                profile.getExperienceLevel(),
                profile.getResumeUrl(),
                profile.getPortfolioUrl(),
                profile.getLinkedinUrl()
        );

        UserEducation edu = profile.getEducation();
        Stream<Object> educationFields = edu == null ? Stream.empty() : Stream.of(
                edu.getHighestEducation(),
                edu.getSpecialisation(),
                edu.getInstitute(),
                edu.getLocation(),
                edu.getPassOutYear(),
                edu.getPercentage()
        );

        List<Object> allFields = Stream.concat(profileFields, educationFields).collect(Collectors.toList());
        long total = allFields.size();
        if (total == 0) return 0;
        long filled = allFields.stream()
                .filter(v -> v != null && (!(v instanceof String s) || !s.trim().isEmpty()))
                .count();
        return (int) Math.round((filled * 100.0) / total);
    }

    public void checkUserExists(Long userId) {
        if (!userProfileRepository.existsById(userId)) {
            throw new UserNotFound("User must create profile or user must have a profile");
        }
    }
}