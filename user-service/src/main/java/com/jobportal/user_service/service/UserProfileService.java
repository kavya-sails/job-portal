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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final UserProfileMapper userProfileMapper;
    private final UserEducationMapper userEducationMapper; // <---- ADD THIS


    public UserProfileResponseDto createUserProfile(UserProfileRequestDto dto, Long headerUserId) {

        // Prevent duplicate profiles for same auth user
        if (userProfileRepository.existsById(headerUserId)) {
            throw new DataIntegrityViolationException(
                    "User profile already exists for userId: " + headerUserId
            );
        }
        // Ensure AuthUser exists
        UserCredential authUser = userCredentialRepository.findById(headerUserId)
                .orElseThrow(() -> new UserNotFound(
                        "Auth user not found with id: " + headerUserId
                ));

        try {
            // Map request DTO to entity
            UserProfile userProfile = userProfileMapper.toEntity(dto);
            userProfile.setId(authUser.getUserId());
            recalculateProfileCompletion(userProfile);
            UserProfile saved = userProfileRepository.save(userProfile);
            // Map entity to response DTO and attach email from AuthUser
            UserProfileResponseDto response = userProfileMapper.toResponseDto(saved);
            response.setEmail(authUser.getEmail());

            return response;

        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException(
                    "Profile violates a data constraint"
            );
        }
    }


    public UserProfileResponseDto getUserProfileById(Long pathId, Long headerUserId) {

        if (!pathId.equals(headerUserId)) {
            throw new ForbiddenException(
                    "You are not allowed to access this profile"
            );
        }

        UserProfile userProfile = userProfileRepository.findById(pathId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User profile not found with id: " + pathId)
                );

        UserCredential authUser = userCredentialRepository.findById(pathId)
                .orElseThrow(() -> new UserNotFound(
                        "Auth user not found with id: " + pathId
                ));

        UserProfileResponseDto response = userProfileMapper.toResponseDto(userProfile);
        response.setEmail(authUser.getEmail());

        return response;
    }


    public List<UserProfileResponseDto> getAllUserProfiles() {
        List<UserProfile> users = userProfileRepository.findAll();
        List<UserProfileResponseDto> responseList = userProfileMapper.toResponseDTOList(users);

        // Attach email for each profile from AuthUser
        for (UserProfileResponseDto resp : responseList) {
            userCredentialRepository.findById(resp.getId()).ifPresent(
                    authUser -> resp.setEmail(authUser.getEmail())
            );
        }

        return responseList;
    }


    public void deleteUserProfileById(Long pathId, Long headerUserId) {

        if (!pathId.equals(headerUserId)) {
            throw new ForbiddenException(
                    "You are not allowed to delete this profile"
            );
        }

        UserProfile userProfile = userProfileRepository.findById(pathId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User profile not found with id: " + pathId)
                );

        userProfileRepository.delete(userProfile);
    }


    public UserProfileResponseDto updateUserProfile(Long pathId, Long headerUserId, UserProfileRequestDto dto) {

        if (!pathId.equals(headerUserId)) {
            throw new ForbiddenException(
                    "You are not allowed to update this profile"
            );
        }

        UserProfile userProfile = userProfileRepository.findById(pathId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User profile not found with id: " + pathId)
                );

        String oldResumeUrl = userProfile.getResumeUrl();

        UserCredential authUser = userCredentialRepository.findById(pathId)
                .orElseThrow(() -> new UserNotFound(
                        "Auth user not found with id: " + pathId
                ));

        try {
            // Map scalar fields, but NOT education (we ignored it in mapper)
            userProfileMapper.updateEntityFromDto(dto, userProfile);

            // ----- handle education manually -----
            if (dto.getEducation() != null) {
                if (userProfile.getEducation() == null) {
                    // no education yet -> create new
                    UserEducation edu = userEducationMapper.toEntity(dto.getEducation());
                    edu.setUserProfile(userProfile);        // for @MapsId
                    userProfile.setEducation(edu);
                } else {
                    // update existing education in-place
                    userEducationMapper.updateEntityFromDto(
                            dto.getEducation(),
                            userProfile.getEducation()
                    );
                }
            }

            updateResumeTimestampIfChanged(oldResumeUrl, userProfile.getResumeUrl(), userProfile);


            recalculateProfileCompletion(userProfile);

            UserProfile updatedUser = userProfileRepository.save(userProfile);

            UserProfileResponseDto response = userProfileMapper.toResponseDto(updatedUser);
            response.setEmail(authUser.getEmail());

            return response;

        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            // optionally log the root cause to see exact DB error
            // ex.getMostSpecificCause().printStackTrace();
            throw new DataIntegrityViolationException(
                    "Profile violates a data constraint"
            );
        }
    }



    public UserProfileResponseDto partialUpdateUserProfile(Long pathId, Long headerUserId, UserProfilePartialUpdateDto dto) {
        if (!pathId.equals(headerUserId)) {
            throw new ForbiddenException(
                    "You are not allowed to update this profile"
            );
        }

        UserProfile userProfile = userProfileRepository.findById(pathId)
                .orElseThrow(() ->
                        new UserProfileNotFoundException("User profile not found with id: " + pathId)
                );

        String oldResumeUrl = userProfile.getResumeUrl();


        UserCredential authUser = userCredentialRepository.findById(pathId)
                .orElseThrow(() -> new UserNotFound(
                        "Auth user not found with id: " + pathId
                ));

        try {
            userProfileMapper.patchEntityFromDto(dto, userProfile);

            // handle education in PATCH as well
            if (dto.getEducation() != null) {
                if (userProfile.getEducation() == null) {
                    UserEducation edu = userEducationMapper.toEntity(dto.getEducation());
                    edu.setUserProfile(userProfile);
                    userProfile.setEducation(edu);
                } else {
                    userEducationMapper.updateEntityFromDto(
                            dto.getEducation(),
                            userProfile.getEducation()
                    );
                }
            }

            updateResumeTimestampIfChanged(oldResumeUrl, userProfile.getResumeUrl(), userProfile);


            recalculateProfileCompletion(userProfile);

            UserProfile updatedUser = userProfileRepository.save(userProfile);

            UserProfileResponseDto response = userProfileMapper.toResponseDto(updatedUser);
            response.setEmail(authUser.getEmail());

            return response;

        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException(
                    "Profile violates a data constraint"
            );
        }
    }



    private void recalculateProfileCompletion(UserProfile profile) {
        int percentage = calculateProfileCompletionPercentage(profile);
        profile.setProfileCompletionPercentage(percentage);
    }

    /**
     * Calculate completion based on non-empty user-facing fields only.
     * Ignores technical fields like id, createdAt, updatedAt, resumeUploadedAt.
     */
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

        List<Object> allFields = Stream.concat(profileFields, educationFields).toList();

        long total = allFields.size();
        if (total == 0) return 0;

        long filled = allFields.stream()
                .filter(v -> v != null && (!(v instanceof String s) || !s.trim().isEmpty()))
                .count();

        return (int) Math.round((filled * 100.0) / total);
    }

    private void updateResumeTimestampIfChanged(String oldUrl, String newUrl, UserProfile profile) {
        if (newUrl != null && !newUrl.equals(oldUrl)) {
            profile.setResumeUploadedAt(LocalDateTime.now());
        }
    }

}
