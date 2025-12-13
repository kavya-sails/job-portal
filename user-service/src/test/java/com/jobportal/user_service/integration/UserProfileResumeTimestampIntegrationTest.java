package com.jobportal.user_service.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.user_service.dto.EducationDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Transactional
@Import(TestBeansConfig.class)
public class UserProfileResumeTimestampIntegrationTest extends IntegrationTestBase {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;

    private UserProfileRequestDto dto(String resumeUrl, String institute) {
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
                .resumeUrl(resumeUrl)
                .portfolioUrl("https://example.com/portfolio")
                .linkedinUrl("https://example.com/in/john")
                .education(EducationDto.builder()
                        .highestEducation(EducationLevel.BACHELORS)
                        .specialisation(Specialisation.COMPUTER_SCIENCE)
                        .institute(institute)
                        .location("City")
                        .passOutYear(2022)
                        .percentage(80.5)
                        .build())
                .build();
    }

    @Test
    void put_sameResumeUrl_doesNotChangeResumeUploadedAt_butEducationUpdates() throws Exception {
        UserCredential uc = createCredential("resume@test.com", "Abcd@1234", RoleName.USER, true);

        // create
        String createResp = mockMvc.perform(post("/api/users/profile/create")
                        .header("X-User-Id", uc.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto("https://example.com/r1.pdf", "Institute-1"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode created = mapper.readTree(createResp);
        String ts1 = created.get("resumeUploadedAt").asText();
        assertThat(ts1).isNotBlank();

        // PUT update with SAME resumeUrl but changed institute (education update path)
        String updateResp = mockMvc.perform(put("/api/users/profile/{id}", uc.getUserId())
                        .header("X-User-Id", uc.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto("https://example.com/r1.pdf", "Institute-UPDATED"))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode updated = mapper.readTree(updateResp);
        String ts2 = updated.get("resumeUploadedAt").asText();

        // should not change if resumeUrl is same
        assertThat(ts2).isEqualTo(ts1);

        // education should update
        assertThat(updated.get("education").get("institute").asText()).isEqualTo("Institute-UPDATED");
    }

    @Test
    void put_newResumeUrl_changesResumeUploadedAt() throws Exception {
        UserCredential uc = createCredential("resume2@test.com", "Abcd@1234", RoleName.USER, true);

        String createResp = mockMvc.perform(post("/api/users/profile/create")
                        .header("X-User-Id", uc.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto("https://example.com/old.pdf", "Institute"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String ts1 = mapper.readTree(createResp).get("resumeUploadedAt").asText();

        String updateResp = mockMvc.perform(put("/api/users/profile/{id}", uc.getUserId())
                        .header("X-User-Id", uc.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto("https://example.com/new.pdf", "Institute"))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String ts2 = mapper.readTree(updateResp).get("resumeUploadedAt").asText();

        assertThat(ts2).isNotEqualTo(ts1);
    }
}
