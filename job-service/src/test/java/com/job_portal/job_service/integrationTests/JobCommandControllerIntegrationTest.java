package com.job_portal.job_service.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.job_portal.job_service.dto.command.JobCommandDto;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Transactional
public class JobCommandControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JobQueryRepository jobRepo;

    @Test
    public void create_update_delete_job_flow() throws Exception {
        JobCommandDto dto = JobCommandDto.builder()
                .title("Integration Job")
                .description("A long enough description that satisfies validation checks")
                .location("Bangalore")
                .experienceRequired(2)
                .companyName("MyCo")
                .packageOffered("6 LPA")
                .skills("Java, Spring")
                .education("B.Tech")
                .postedDate(Instant.now())
                .expiryDays(30)
                .build();

        // create
        String resp = mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Job"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // parse created id
        Long id = objectMapper.readTree(resp).get("jobId").asLong();
        assertThat(jobRepo.findById(id)).isPresent();

        // update title
        dto.setTitle("Updated Title");
        mockMvc.perform(put("/api/jobs/{jobId}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));

        // delete
        mockMvc.perform(delete("/api/jobs/{jobId}", id))
                .andExpect(status().isNoContent());

        assertThat(jobRepo.findById(id)).isNotPresent();
    }

    @Test
    public void create_job_invalidValidation_returnsBadRequest() throws Exception {
        JobCommandDto dto = JobCommandDto.builder()
                .title("x") // too short
                .description("short")
                .location("L")
                .experienceRequired(-1)
                .companyName("") // invalid
                .packageOffered("")
                .skills("s")
                .education("")
                .expiryDays(0)
                .build();

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}