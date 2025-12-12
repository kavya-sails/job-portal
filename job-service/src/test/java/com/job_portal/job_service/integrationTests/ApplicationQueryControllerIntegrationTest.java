package com.job_portal.job_service.integrationTests;

import com.job_portal.job_service.client.UserClient;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.repository.command.ApplicationCommandRepository;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Transactional
public class ApplicationQueryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobQueryRepository jobRepo;

    @Autowired
    private ApplicationCommandRepository appRepo;

    @MockBean
    private UserClient userClient; // keep context lightweight by mocking external client

    private JobEntity job;

    @BeforeEach
    public void setup() {
        JobEntity j = JobEntity.builder()
                .title("Query Job")
                .description("desc")
                .location("City")
                .experienceRequired(2)
                .companyName("Comp")
                .packageOffered("5 LPA")
                .skills("skill")
                .education("edu")
                .postedDate(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        job = jobRepo.save(j);
    }

    @Test
    public void getHistory_missingHeader_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/jobs/applications/history/{userId}", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Missing Required Header"));
    }

    @Test
    public void getHistory_forbidden_whenHeaderUserDifferentAndNotAdmin() throws Exception {
        mockMvc.perform(get("/api/jobs/applications/history/{userId}", 2L)
                        .header("X-User-Id", 3L)
                        .header("X-User-Role", "USER")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }
}