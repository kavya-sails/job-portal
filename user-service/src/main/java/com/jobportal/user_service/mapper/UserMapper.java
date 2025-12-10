package com.jobportal.user_service.mapper;

import com.jobportal.user_service.dto.EducationDto;
import com.jobportal.user_service.dto.UserPartialUpdateDto;
import com.jobportal.user_service.dto.UserRequestDto;
import com.jobportal.user_service.dto.UserResponseDto;
import com.jobportal.user_service.entity.UserEducation;
import com.jobportal.user_service.entity.UserProfile;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // --------------- CREATE ---------------

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "profileCompletionPercentage", ignore = true),
            @Mapping(target = "resumeUploadedAt",
                    expression = "java(dto.getResumeUrl() != null ? java.time.LocalDateTime.now() : null)")
    })
    UserProfile toEntity(UserRequestDto dto);

    // --------------- DTO <-> ENTITY FOR EDUCATION ---------------

    UserEducation educationDtoToUserEducation(EducationDto dto);

    EducationDto userEducationToEducationDto(UserEducation entity);

    // --------------- READ ---------------

    UserResponseDto toResponseDto(UserProfile user);

    List<UserResponseDto> toResponseDTOList(List<UserProfile> users);

    // --------------- FULL UPDATE ---------------

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "profileCompletionPercentage", ignore = true),
            @Mapping(target = "resumeUploadedAt",
                    expression = "java(updateResumeTimestamp(dto, entity))")
    })
    void updateEntityFromDto(UserRequestDto dto, @MappingTarget UserProfile entity);

    // --------------- PARTIAL UPDATE ---------------

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "profileCompletionPercentage", ignore = true),
            @Mapping(target = "resumeUploadedAt",
                    expression = "java(updateResumeTimestampOnPatch(dto, entity))")
    })
    void patchEntityFromDto(UserPartialUpdateDto dto, @MappingTarget UserProfile entity);

    // --------------- HELPERS ---------------

    default LocalDateTime updateResumeTimestamp(UserRequestDto dto, UserProfile entity) {
        if (dto.getResumeUrl() != null && !dto.getResumeUrl().equals(entity.getResumeUrl())) {
            return LocalDateTime.now();
        }
        return entity.getResumeUploadedAt();
    }

    default LocalDateTime updateResumeTimestampOnPatch(UserPartialUpdateDto dto, UserProfile entity) {
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
