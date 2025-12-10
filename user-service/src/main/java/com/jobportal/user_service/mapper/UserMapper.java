package com.jobportal.user_service.mapper;

import com.jobportal.user_service.dto.UserPartialUpdateDto;
import com.jobportal.user_service.dto.UserRequestDto;
import com.jobportal.user_service.dto.UserResponseDto;
import com.jobportal.user_service.entity.UserProfile;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // RequestDto to Entity
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "resumeUploadedAt",
                    expression = "java(dto.getResumeUrl() != null ? java.time.LocalDateTime.now() : null)"),
            @Mapping(target = "isActive", constant = "true"),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    UserProfile toEntity(UserRequestDto dto);

    // Entity to ResponseDto (email set in service)
    UserResponseDto toResponseDto(UserProfile user);

    // List of Entities to List of ResponseDto
    List<UserResponseDto> toResponseDTOList(List<UserProfile> users);

    // Update existing entity from Request Dto
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "isActive", ignore = true),
            @Mapping(target = "resumeUploadedAt",
                    expression = "java(updateResumeTimestamp(dto, entity))")
    })
    void updateEntityFromDto(UserRequestDto dto, @MappingTarget UserProfile entity);

    // Helper to update resumeUploadedAt only when URL changes
    default LocalDateTime updateResumeTimestamp(UserRequestDto dto, UserProfile entity) {
        if (dto.getResumeUrl() != null && !dto.getResumeUrl().equals(entity.getResumeUrl())) {
            return LocalDateTime.now();
        }
        return entity.getResumeUploadedAt();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    void patchEntityFromDto(UserPartialUpdateDto dto, @MappingTarget UserProfile entity);

}
