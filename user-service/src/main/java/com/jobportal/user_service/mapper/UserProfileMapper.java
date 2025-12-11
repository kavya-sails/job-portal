package com.jobportal.user_service.mapper;

import com.jobportal.user_service.dto.UserProfilePartialUpdateDto;
import com.jobportal.user_service.dto.UserProfileRequestDto;
import com.jobportal.user_service.dto.UserProfileResponseDto;
import com.jobportal.user_service.entity.UserProfile;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserEducationMapper.class})
public interface UserProfileMapper {

    // CREATE is fine: we can still map education here normally
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "profileCompletionPercentage", ignore = true),
            @Mapping(target = "resumeUploadedAt",
                    expression = "java(dto.getResumeUrl() != null ? java.time.LocalDateTime.now() : null)")
    })
    UserProfile toEntity(UserProfileRequestDto dto);

    UserProfileResponseDto toResponseDto(UserProfile user);
    List<UserProfileResponseDto> toResponseDTOList(List<UserProfile> users);

    // FULL UPDATE (PUT)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "profileCompletionPercentage", ignore = true),
            @Mapping(target = "resumeUploadedAt",
                    expression = "java(updateResumeTimestamp(dto, entity))"),
            @Mapping(target = "education", ignore = true) // <---- IMPORTANT
    })
    void updateEntityFromDto(UserProfileRequestDto dto, @MappingTarget UserProfile entity);

    // PARTIAL UPDATE (PATCH)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "profileCompletionPercentage", ignore = true),
            @Mapping(target = "resumeUploadedAt",
                    expression = "java(updateResumeTimestampOnPatch(dto, entity))"),
            @Mapping(target = "education", ignore = true) // <---- IMPORTANT
    })
    void patchEntityFromDto(UserProfilePartialUpdateDto dto, @MappingTarget UserProfile entity);


    // --------------- HELPERS ---------------

    default LocalDateTime updateResumeTimestamp(UserProfileRequestDto dto, UserProfile entity) {
        if (dto.getResumeUrl() != null && !dto.getResumeUrl().equals(entity.getResumeUrl())) {
            return LocalDateTime.now();
        }
        return entity.getResumeUploadedAt();
    }

    default LocalDateTime updateResumeTimestampOnPatch(UserProfilePartialUpdateDto dto, UserProfile entity) {
        if (dto.getResumeUrl() != null && !dto.getResumeUrl().equals(entity.getResumeUrl())) {
            return LocalDateTime.now();
        }
        return entity.getResumeUploadedAt();
    }

    @AfterMapping
    default void setUserEducationBackRef(@MappingTarget UserProfile entity) {
        if (entity.getEducation() != null) {
            entity.getEducation().setUserProfile(entity);
        }
    }
}
