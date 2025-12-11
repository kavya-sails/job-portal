package com.job_portal.job_service.integration;

import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import java.time.Instant;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JobControllerIntegrationTest {
    private final MockMvc mockMvc;
    private final JobQueryRepository jobRepository;

    @BeforeEach
    void cleanDb() {
        jobRepository.deleteAll();
    }

    private JobEntity persistJob(String title, String location, int experience) {
        JobEntity job = new JobEntity();
        job.setTitle(title);
        job.setDescription("Description for " + title);
        job.setLocation(location);
        job.setExperienceRequired(experience);
        job.setEducation("Bachelor's Degree");
        job.setPostedDate(Instant.now());
        job.setCompanyName("Test Company");
        job.setPackageOffered("8LPA");
        job.setSkills("Java, Spring Boot");
        job.setExpiresAt(Instant.now().plusSeconds(3600L * 24 * 30)); // 30 days later
        return jobRepository.save(job);
    }

    // ---------- Negative tests ----------

    @Test
    void createJob_success_returnsCreatedJob() throws Exception {
        String payload = """
            {
              "title": "Backend Developer",
              "description": "Build APIs",
              "location": "Bengaluru",
              "experienceRequired": 3,
              "companyName": "Tech Corp",
              "description": "Responsible for server-side web application logic.",
              "expiryDays": 10
            }
            """;

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk()) // controller returns ResponseEntity.ok(created)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jobId").isNumber())
                .andExpect(jsonPath("$.title").value("Backend Developer"))
                .andExpect(jsonPath("$.location").value("Bengaluru"));

        // assert saved in DB
        List<JobEntity> all = jobRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.getFirst().getTitle()).isEqualTo("Backend Developer");
    }

    @Test
    void getJob_success_returnsJobDetails() throws Exception {
        JobEntity saved = persistJob("Frontend Dev", "Remote", 2);

        mockMvc.perform(get("/api/jobs/{id}", saved.getJobId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.title").value("Frontend Dev"))
                .andExpect((ResultMatcher) jsonPath("$.location").value("Remote"));
    }

    @Test
    void updateJob_success_updatesAndReturns() throws Exception {
        JobEntity saved = persistJob("QA Engineer", "Pune", 1);

        String updatePayload = """
            {
              "title": "QA Engineer - Automation",
              "description": "Selenium, Playwright",
              "location": "Pune",
              "experienceRequired": 2
            }
            """;

        mockMvc.perform(put("/api/jobs/{id}", saved.getJobId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("QA Engineer - Automation"))
                .andExpect(jsonPath("$.experienceRequired").value(2));

        JobEntity refreshed = jobRepository.findById(saved.getJobId()).orElseThrow();
        assertThat(refreshed.getTitle()).isEqualTo("QA Engineer - Automation");
        assertThat(refreshed.getExperienceRequired()).isEqualTo(2);
    }

    @Test
    void deleteJob_success_removesJob() throws Exception {
        JobEntity saved = persistJob("DevOps", "Chennai", 4);

        mockMvc.perform(delete("/api/jobs/{id}", saved.getJobId()))
                .andExpect(status().isNoContent());

        assertThat(jobRepository.existsById(saved.getJobId())).isFalse();
    }

    @Test
    void searchJobs_withFilters_andPagination_returnsPage() throws Exception {
        // create 5 jobs, 3 with title 'Java'
        persistJob("Java Backend", "Bengaluru", 3);
        persistJob("Java Architect", "Bengaluru", 8);
        persistJob("Java Intern", "Remote", 0);
        persistJob("Python Dev", "Bengaluru", 2);
        persistJob("Go Developer", "Remote", 4);

        // find Java with page size 2
        mockMvc.perform(get("/api/jobs/search")
                        .param("title", "Java")
                        .param("page", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // expecting a Page of DTOs serialized by Spring Data Page -> JSON structure depends on your controller return
                // Here we just assert the response contains content array and at least one of the Java titles.
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").exists());
    }

    // ---------- Negative tests ----------

    @Test
    void createJob_validationFails_returnsBadRequest() throws Exception {
        // missing title (assuming title is @NotBlank)
        String payload = """
            {
              "title": "",
              "description": "desc",
              "location": "Remote",
              "experienceRequired": 1
            }
            """;

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getJob_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/jobs/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateJob_notFound_returns404() throws Exception {
        String updatePayload = """
            {
              "title": "Non Existent",
              "description": "x",
              "location": "Nowhere",
              "experienceRequired": 0
            }
            """;

        mockMvc.perform(put("/api/jobs/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteJob_notFound_returns404() throws Exception {
        mockMvc.perform(delete("/api/jobs/{id}", 99999L))
                .andExpect(status().isNotFound());
    }
}