package com.jobportal.user_service.integration;

import com.jobportal.user_service.entity.UserCredential;
import com.jobportal.user_service.enums.RoleName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Transactional
@Import(TestBeansConfig.class)
public class GlobalExceptionHandlerIntegrationTest extends IntegrationTestBase {

    @Autowired MockMvc mockMvc;

    @Test
    void createProfile_invalidEnum_returns400_withNiceMessage() throws Exception {
        UserCredential uc = createCredential("enum@test.com", "Abcd@1234", RoleName.USER, true);

        // jobRole is enum -> invalid value should trigger HttpMessageNotReadableException->InvalidFormatException
        String body = """
        {
          "firstName":"John",
          "lastName":"Doe",
          "dob":"2000-01-01",
          "address":"Addr",
          "phone":"9876543210",
          "skills":"Java,Spring",
          "experience":2,
          "jobRole":"NOT_A_REAL_ROLE",
          "experienceLevel":"JUNIOR",
          "resumeUrl":"https://example.com/resume.pdf",
          "portfolioUrl":"https://example.com/portfolio",
          "linkedinUrl":"https://example.com/in/john",
          "education":{
            "highestEducation":"BACHELORS",
            "specialisation":"COMPUTER_SCIENCE",
            "institute":"Institute",
            "location":"City",
            "passOutYear":2022,
            "percentage":80.5
          }
        }
        """;

        mockMvc.perform(post("/api/users/profile/create")
                        .header("X-User-Id", uc.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid Request Body"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createProfile_malformedJson_returns400() throws Exception {
        UserCredential uc = createCredential("json@test.com", "Abcd@1234", RoleName.USER, true);

        // Missing closing braces + bad JSON
        String badJson = """
        {
          "firstName":"John",
          "lastName":"Doe"
        """;

        mockMvc.perform(post("/api/users/profile/create")
                        .header("X-User-Id", uc.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid Request Body"))
                .andExpect(jsonPath("$.message").exists());
    }
}
