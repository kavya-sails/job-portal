package com.job_portal.job_service.integration;

import com.job_portal.job_service.entity.ApplicationEntity;
import com.job_portal.job_service.entity.ApplicationStatus;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.repository.command.ApplicationCommandRepository;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import com.job_portal.job_service.repository.query.ApplicationQueryRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ApplicationCommandController and ApplicationQueryController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ApplicationControllerIntegrationTest {

    private final MockMvc mockMvc;
    private final JobQueryRepository jobRepository;
    private final ApplicationCommandRepository applicationCommandRepository;
    private final ApplicationQueryRepository applicationQueryRepository;

    @BeforeEach
    void cleanup() {
        applicationCommandRepository.deleteAll();
        applicationQueryRepository.deleteAll();
        jobRepository.deleteAll();
    }

    private JobEntity persistJob(String title) {
        JobEntity job = new JobEntity();
        job.setTitle(title);
        job.setDescription("Desc " + title);
        job.setLocation("Remote");
        job.setExperienceRequired(2);
        job.setEducation("Bachelor's Degree");
        job.setPackageOffered("8LPA");
        job.setSkills("Java, Spring Boot");
        job.setCompanyName("ACME");
        job.setPostedDate(Instant.now());
        job.setExpiresAt(Instant.now().plusSeconds(3600L * 24 * 10));
        return jobRepository.save(job);
    }

    private ApplicationEntity persistApplication(String userId, JobEntity job, ApplicationStatus status) {
        ApplicationEntity app = ApplicationEntity.builder()
                .job(job)
                .userId(userId)
                .companyName(job.getCompanyName())
                .appliedDate(Instant.now())
                .status(status)
                .build();
        return applicationCommandRepository.save(app);
    }

    // --- Positive tests ---

    @Test
    void apply_success_returnsApplicationHistoryDto() throws Exception {
        JobEntity job = persistJob("Backend");

        // call apply with header X-User-Id
        mockMvc.perform(post("/api/jobs/applications/{jobId}", job.getJobId())
                        .header("X-User-Id", "user-123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // expected fields in ApplicationHistoryQueryDto
                .andExpect(jsonPath("$.applicationId").isNumber())
                .andExpect(jsonPath("$.jobId").value(job.getJobId()))
                .andExpect(jsonPath("$.jobTitle").value("Backend"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        // persisted in DB
        List<ApplicationEntity> all = applicationCommandRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getUserId()).isEqualTo("user-123");
        assertThat(all.get(0).getJob().getJobId()).isEqualTo(job.getJobId());
    }

    @Test
    void updateStatus_success_updatesAndReturns() throws Exception {
        JobEntity job = persistJob("SRE");
        ApplicationEntity app = persistApplication("user-A", job, ApplicationStatus.PENDING);

        String body = """
            {"status": "REVIEWED"}
            """;

        mockMvc.perform(put("/api/jobs/applications/{applicationId}/status", app.getApplicationId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicationId").value(app.getApplicationId()))
                .andExpect(jsonPath("$.status").value("REVIEWED"));

        ApplicationEntity refreshed = applicationCommandRepository.findById(app.getApplicationId()).orElseThrow();
        assertThat(refreshed.getStatus()).isEqualTo(ApplicationStatus.REVIEWED);
    }

    @Test
    void getHistory_owner_canFetchHistory() throws Exception {
        JobEntity job1 = persistJob("Job-A");
        JobEntity job2 = persistJob("Job-B");

        persistApplication("owner-1", job1, ApplicationStatus.PENDING);
        persistApplication("owner-1", job2, ApplicationStatus.SELECTED);
        // another user's application (should not be returned)
        persistApplication("other", job1, ApplicationStatus.PENDING);

        mockMvc.perform(get("/api/jobs/applications/history/{userId}", "owner-1")
                        .header("X-User-Id", "owner-1")
                        .header("X-User-Role", "USER")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value("owner-1"))
                .andExpect(jsonPath("$[1].userId").value("owner-1"));
    }

    @Test
    void getHistory_admin_canFetchOtherUserHistory() throws Exception {
        JobEntity job = persistJob("AdminJob");
        persistApplication("target-user", job, ApplicationStatus.PENDING);

        mockMvc.perform(get("/api/jobs/applications/history/{userId}", "target-user")
                        .header("X-User-Id", "some-admin")
                        .header("X-User-Role", "ADMIN")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // --- Negative tests ---

    @Test
    void apply_missingUserIdHeader_returnsBadRequest() throws Exception {
        JobEntity job = persistJob("NoHeader");

        mockMvc.perform(post("/api/jobs/applications/{jobId}", job.getJobId())
                        // no X-User-Id header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void apply_duplicateApplication_returnsConflict() throws Exception {
        JobEntity job = persistJob("DupJob");
        // user already applied
        persistApplication("dup-user", job, ApplicationStatus.PENDING);

        mockMvc.perform(post("/api/jobs/applications/{jobId}", job.getJobId())
                        .header("X-User-Id", "dup-user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void updateStatus_notFound_returns404() throws Exception {
        String body = """
            {"status":"REJECTED"}
            """;

        mockMvc.perform(put("/api/jobs/applications/{applicationId}/status", 999999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void getHistory_missingHeader_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/jobs/applications/history/{userId}", "u1")
                        // missing X-User-Id header
                        .header("X-User-Role", "USER"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getHistory_forbiddenWhenNotOwnerAndNotAdmin_returnsForbidden() throws Exception {
        JobEntity job = persistJob("F");
        persistApplication("target", job, ApplicationStatus.PENDING);

        mockMvc.perform(get("/api/jobs/applications/history/{userId}", "target")
                        .header("X-User-Id", "another-user")
                        .header("X-User-Role", "USER") // not ADMIN
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}

