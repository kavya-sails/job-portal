package com.jobportal.user_service.mapper;

import com.jobportal.user_service.dto.EducationDto;
import com.jobportal.user_service.entity.UserEducation;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserEducationMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "userProfile", ignore = true)
    })
    UserEducation toEntity(EducationDto dto);

    EducationDto toDto(UserEducation entity);

    // NEW: update existing entity instead of creating a new one
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "userProfile", ignore = true)
    })
    void updateEntityFromDto(EducationDto dto, @MappingTarget UserEducation entity);
}
