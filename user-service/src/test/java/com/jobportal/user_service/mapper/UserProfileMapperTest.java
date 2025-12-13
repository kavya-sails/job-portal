package com.jobportal.user_service.mapper;

import com.jobportal.user_service.dto.UserProfilePartialUpdateDto;
import com.jobportal.user_service.dto.UserProfileRequestDto;
import com.jobportal.user_service.entity.UserEducation;
import com.jobportal.user_service.entity.UserProfile;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileMapperTest {

    // Minimal test implementation: only what's needed for tests.
    static class SimpleUserProfileMapper implements UserProfileMapper {
        @Override
        public UserProfile toEntity(UserProfileRequestDto dto) {
            UserProfile u = new UserProfile();
            u.setFirstName(dto.getFirstName());
            u.setLastName(dto.getLastName());
            u.setResumeUrl(dto.getResumeUrl());
            return u;
        }

        @Override
        public com.jobportal.user_service.dto.UserProfileResponseDto toResponseDto(UserProfile user) {
            var r = new com.jobportal.user_service.dto.UserProfileResponseDto();
            r.setId(user.getId());
            r.setFirstName(user.getFirstName());
            r.setResumeUrl(user.getResumeUrl());
            r.setResumeUploadedAt(user.getResumeUploadedAt());
            return r;
        }

        @Override
        public java.util.List<com.jobportal.user_service.dto.UserProfileResponseDto> toResponseDTOList(java.util.List<UserProfile> users) {
            return users.stream().map(this::toResponseDto).toList();
        }

        @Override
        public void updateEntityFromDto(UserProfileRequestDto dto, UserProfile entity) {
            if (dto.getFirstName() != null) entity.setFirstName(dto.getFirstName());
            if (dto.getLastName() != null) entity.setLastName(dto.getLastName());
            if (dto.getResumeUrl() != null) entity.setResumeUrl(dto.getResumeUrl());
        }

        @Override
        public void patchEntityFromDto(UserProfilePartialUpdateDto dto, UserProfile entity) {
            if (dto.getFirstName() != null) entity.setFirstName(dto.getFirstName());
            if (dto.getResumeUrl() != null) entity.setResumeUrl(dto.getResumeUrl());
        }
    }

    private final SimpleUserProfileMapper mapper = new SimpleUserProfileMapper();

    @Test
    void resumeTimestamp_updates_when_url_changes() {
        var dto = new UserProfileRequestDto();
        dto.setResumeUrl("https://new/resume");

        var user = new UserProfile();
        user.setResumeUrl("https://old/resume");
        user.setResumeUploadedAt(null);

        LocalDateTime ts = mapper.updateResumeTimestamp(dto, user);
        assertNotNull(ts);
    }

    @Test
    void resumeTimestamp_preserved_when_url_same() {
        var dto = new UserProfileRequestDto();
        dto.setResumeUrl("https://same");

        var user = new UserProfile();
        var now = LocalDateTime.of(2022,1,1,0,0);
        user.setResumeUrl("https://same");
        user.setResumeUploadedAt(now);

        assertEquals(now, mapper.updateResumeTimestamp(dto, user));
    }

    @Test
    void afterMapping_sets_education_backref() {
        var profile = new UserProfile();
        profile.setId(7L);
        var edu = new UserEducation();
        profile.setEducation(edu);

        mapper.setUserEducationBackRef(profile);
        assertSame(profile, profile.getEducation().getUserProfile());
    }
}
