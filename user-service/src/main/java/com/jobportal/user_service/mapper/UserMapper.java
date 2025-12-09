package com.jobportal.user_service.mapper;

import com.jobportal.user_service.dto.UserRequestDto;
import com.jobportal.user_service.dto.UserResponseDto;
import com.jobportal.user_service.entity.UserData;
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
    UserData toEntity(UserRequestDto dto);

    //Entity to ResponseDto
    UserResponseDto toResponseDto(UserData user);

    // List of Entities to List of ResponseDto
    List<UserResponseDto> toResponseDTOList(List<UserData> users);

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
    void updateEntityFromDto(UserRequestDto dto, @MappingTarget UserData entity);

    // Helper to update resumeUploadedAt only when URL changes
    default LocalDateTime updateResumeTimestamp(UserRequestDto dto, UserData entity) {
        if (dto.getResumeUrl() != null && !dto.getResumeUrl().equals(entity.getResumeUrl())) {
            return LocalDateTime.now();
        }
        return entity.getResumeUploadedAt();
    }
}
