package com.jobportal.user_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.user_service.dto.EducationDto;
import com.jobportal.user_service.dto.UserProfilePartialUpdateDto;
import com.jobportal.user_service.dto.UserProfileRequestDto;
import com.jobportal.user_service.entity.UserCredential;
import com.jobportal.user_service.enums.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Transactional
@Import(TestBeansConfig.class)
public class UserProfileIntegrationTest extends IntegrationTestBase {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private UserProfileRequestDto validDto() {
        return UserProfileRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .dob(LocalDate.of(2000, 1, 1))
                .address("Address")
                .phone("9876543210")
                .skills("Java,Spring")
                .experience(2)
                .jobRole(JobRole.BACKEND_DEVELOPER)
                .experienceLevel(ExperienceLevel.JUNIOR)
                .resumeUrl("https://example.com/resume.pdf")
                .portfolioUrl("https://example.com/portfolio")
                .linkedinUrl("https://example.com/in/john")
                .education(EducationDto.builder()
                        .highestEducation(EducationLevel.BACHELORS)
                        .specialisation(Specialisation.COMPUTER_SCIENCE)
                        .institute("Institute")
                        .location("City")
                        .passOutYear(2022)
                        .percentage(80.5)
                        .build())
                .build();
    }

    @Test
    void createProfile_success_201() throws Exception {
        UserCredential uc = createCredential("p1@example.com", "Abcd@1234", RoleName.USER, true);

        mockMvc.perform(post("/api/users/profile/create")
                        .header("X-User-Id", uc.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(uc.getUserId()))
                .andExpect(jsonPath("$.email").value("p1@example.com"));
    }

    @Test
    void createProfile_validationError_400() throws Exception {
        UserCredential uc = createCredential("bad@example.com", "Abcd@1234", RoleName.USER, true);

        UserProfileRequestDto bad = validDto();
        bad.setPhone("1111111111");     // invalid by regex
        bad.setResumeUrl("bad-url");    // invalid url

        mockMvc.perform(post("/api/users/profile/create")
                        .header("X-User-Id", uc.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    void getProfile_forbidden_403_whenDifferentHeaderUser() throws Exception {
        UserCredential owner = createCredential("owner@example.com", "Abcd@1234", RoleName.USER, true);
        UserCredential other = createCredential("other@example.com", "Abcd@1234", RoleName.USER, true);

        mockMvc.perform(post("/api/users/profile/create")
                        .header("X-User-Id", owner.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto())))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/users/profile/{id}", owner.getUserId())
                        .header("X-User-Id", other.getUserId()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void patchProfile_success_200() throws Exception {
        UserCredential uc = createCredential("patch@example.com", "Abcd@1234", RoleName.USER, true);

        mockMvc.perform(post("/api/users/profile/create")
                        .header("X-User-Id", uc.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto())))
                .andExpect(status().isCreated());

        UserProfilePartialUpdateDto patch = UserProfilePartialUpdateDto.builder()
                .skills("Java,Spring,SQL")
                .portfolioUrl("https://example.com/new_portfolio")
                .build();

        mockMvc.perform(patch("/api/users/profile/{id}", uc.getUserId())
                        .header("X-User-Id", uc.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skills").value("Java,Spring,SQL"));
    }

    @Test
    void deleteProfile_204_thenGet404() throws Exception {
        UserCredential uc = createCredential("del@example.com", "Abcd@1234", RoleName.USER, true);

        mockMvc.perform(post("/api/users/profile/create")
                        .header("X-User-Id", uc.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto())))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/users/profile/{id}", uc.getUserId())
                        .header("X-User-Id", uc.getUserId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/profile/{id}", uc.getUserId())
                        .header("X-User-Id", uc.getUserId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}
