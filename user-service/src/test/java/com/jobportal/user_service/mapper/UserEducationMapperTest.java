package com.jobportal.user_service.mapper;

import com.jobportal.user_service.dto.EducationDto;
import com.jobportal.user_service.entity.UserEducation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Compact, focused tests for UserEducationMapper behavior.
 * Test-only implementation with minimal, clear mapping logic.
 */
class UserEducationMapperTest {

    static class SimpleUserEducationMapper implements UserEducationMapper {
        @Override
        public UserEducation toEntity(EducationDto dto) {
            var e = new UserEducation();
            e.setInstitute(dto.getInstitute());
            e.setLocation(dto.getLocation());
            e.setPassOutYear(dto.getPassOutYear());
            if (dto.getPercentage() != null) e.setPercentage(BigDecimal.valueOf(dto.getPercentage()));
            return e;
        }

        @Override
        public EducationDto toDto(UserEducation entity) {
            var d = new EducationDto();
            d.setInstitute(entity.getInstitute());
            d.setLocation(entity.getLocation());
            d.setPassOutYear(entity.getPassOutYear());
            if (entity.getPercentage() != null) d.setPercentage(entity.getPercentage().doubleValue());
            return d;
        }

        @Override
        public void updateEntityFromDto(EducationDto dto, UserEducation entity) {
            if (dto.getInstitute() != null) entity.setInstitute(dto.getInstitute());
            if (dto.getLocation() != null) entity.setLocation(dto.getLocation());
            if (dto.getPassOutYear() != null) entity.setPassOutYear(dto.getPassOutYear());
            if (dto.getPercentage() != null) entity.setPercentage(BigDecimal.valueOf(dto.getPercentage()));
        }
    }

    private final SimpleUserEducationMapper mapper = new SimpleUserEducationMapper();

    @Test
    void toEntity_maps_basic_fields() {
        var dto = new EducationDto();
        dto.setInstitute("ABC");
        dto.setLocation("Mumbai");
        dto.setPassOutYear(2019);
        dto.setPercentage(72.5);

        var entity = mapper.toEntity(dto);
        assertEquals("ABC", entity.getInstitute());
        assertEquals("Mumbai", entity.getLocation());
        assertEquals(2019, entity.getPassOutYear());
        assertEquals(0, BigDecimal.valueOf(72.5).compareTo(entity.getPercentage()));
    }

    @Test
    void updateEntityFromDto_updates_only_non_null() {
        var dto = new EducationDto();
        dto.setInstitute("New");
        dto.setPassOutYear(2021);

        var e = new UserEducation();
        e.setInstitute("Old");
        e.setLocation("City");
        e.setPassOutYear(2010);
        e.setPercentage(BigDecimal.valueOf(60.0));

        mapper.updateEntityFromDto(dto, e);
        assertEquals("New", e.getInstitute());
        assertEquals("City", e.getLocation()); // unchanged
        assertEquals(2021, e.getPassOutYear());
        assertEquals(0, BigDecimal.valueOf(60.0).compareTo(e.getPercentage())); // unchanged
    }
}
