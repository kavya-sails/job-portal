package com.job_portal.job_service.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.job_portal.job_service.client.UserClient;
import com.job_portal.job_service.config.RabbitMQConfig;
import com.job_portal.job_service.dto.command.ApplicationStatusUpdateDto;
import com.job_portal.job_service.dto.event.ApplicationStatusEvent;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.entity.ApplicationStatus;
import com.job_portal.job_service.repository.command.ApplicationCommandRepository;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Transactional
public class ApplicationCommandControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JobQueryRepository jobRepo;

    @Autowired
    private ApplicationCommandRepository appRepo;

    @MockBean
    private UserClient userClient;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    private JobEntity job;

    @BeforeEach
    public void setup() {
        JobEntity j = JobEntity.builder()
                .title("Test Job")
                .description("Job description for test")
                .location("Remote")
                .experienceRequired(1)
                .companyName("Acme")
                .packageOffered("10 LPA")
                .skills("Java")
                .education("B.E")
                .postedDate(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60 * 60 * 24))
                .build();
        job = jobRepo.save(j);
    }

    @Test
    public void apply_success_returnsHistoryDto_andPersists() throws Exception {
        // user exists
        Mockito.doNothing().when(userClient).checkUserExists(42L);
        mockMvc.perform(post("/api/jobs/applications/{jobId}", job.getJobId())
                        .header("X-User-Id", 42L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value(job.getJobId()))
                .andExpect(jsonPath("$.status").value("PENDING"));

        // persisted
        assertThat(appRepo.findByJobJobIdAndUserId(job.getJobId(), 42L)).isPresent();
    }

    @Test
    public void apply_missingHeader_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/jobs/applications/{jobId}", job.getJobId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Missing Required Header"));
    }

    @Test
    public void apply_userServiceUnavailable_returnsInternalServerError() throws Exception {
        doThrow(new RuntimeException("down")).when(userClient).checkUserExists(99L);
        mockMvc.perform(post("/api/jobs/applications/{jobId}", job.getJobId())
                        .header("X-User-Id", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void updateStatus_valid_updatesAndPublishes() throws Exception {
        // create application first
        Mockito.doNothing().when(userClient).checkUserExists(5L);
        mockMvc.perform(post("/api/jobs/applications/{jobId}", job.getJobId())
                        .header("X-User-Id", 5L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // get persisted application id
        Long appId = appRepo.findByJobJobIdAndUserId(job.getJobId(), 5L).get().getApplicationId();

        // simulate rabbit publish success match the String,String,Object overload
        doNothing().when(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE),
                eq(RabbitMQConfig.ROUTING_KEY),
                any(ApplicationStatusEvent.class)
        );

        ApplicationStatusUpdateDto dto = ApplicationStatusUpdateDto.builder()
                .status(ApplicationStatus.REVIEWED).build();
        mockMvc.perform(put("/api/jobs/applications/{applicationId}/status", appId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationId").value(appId))
                .andExpect(jsonPath("$.status").value("REVIEWED"));
    }

    @Test
    public void updateStatus_publishFailure_returnsInternalServerError() throws Exception {
        Mockito.doNothing().when(userClient).checkUserExists(6L);
        mockMvc.perform(post("/api/jobs/applications/{jobId}", job.getJobId())
                        .header("X-User-Id", 6L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Long appId = appRepo.findByJobJobIdAndUserId(job.getJobId(), 6L).get().getApplicationId();

        // make RabbitTemplate throw on the specific overload
        doThrow(new RuntimeException("rabbit down")).when(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE),
                eq(RabbitMQConfig.ROUTING_KEY),
                any(ApplicationStatusEvent.class)
        );
        ApplicationStatusUpdateDto dto = ApplicationStatusUpdateDto.builder()
                .status(ApplicationStatus.SELECTED).build();
        mockMvc.perform(put("/api/jobs/applications/{applicationId}/status", appId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Message Publish Failed"));
    }
}