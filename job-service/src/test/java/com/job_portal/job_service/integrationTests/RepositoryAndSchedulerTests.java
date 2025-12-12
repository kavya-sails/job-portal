package com.job_portal.job_service.integrationTests;

import com.job_portal.job_service.entity.ApplicationEntity;
import com.job_portal.job_service.entity.ApplicationStatus;
import com.job_portal.job_service.entity.JobEntity;
import com.job_portal.job_service.repository.command.ApplicationCommandRepository;
import com.job_portal.job_service.repository.query.JobQueryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:jobdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL"
})
@ActiveProfiles("test")
public class RepositoryAndSchedulerTests {

    @Autowired
    JobQueryRepository jobRepo;

    @Autowired
    ApplicationCommandRepository appRepo;

    @Test
    public void uniqueConstraint_willPreventDuplicateApplication() {
        JobEntity job = JobEntity.builder()
                .title("UC")
                .description("d")
                .location("L")
                .experienceRequired(1)
                .companyName("C")
                .packageOffered("P")
                .skills("s")
                .education("e")
                .postedDate(Instant.now())
                .expiresAt(Instant.now().plusSeconds(1000))
                .build();
        job = jobRepo.save(job);

        ApplicationEntity a1 = ApplicationEntity.builder()
                .job(job)
                .userId(50L)
                .companyName(job.getCompanyName())
                .appliedDate(Instant.now())
                .status(ApplicationStatus.PENDING)
                .build();
        appRepo.saveAndFlush(a1);

        ApplicationEntity a2 = ApplicationEntity.builder()
                .job(job)
                .userId(50L)
                .companyName(job.getCompanyName())
                .appliedDate(Instant.now())
                .status(ApplicationStatus.PENDING)
                .build();

        // H2 + Hibernate should raise DataIntegrityViolationException for unique constraint violation
        assertThrows(DataIntegrityViolationException.class, () -> appRepo.saveAndFlush(a2));
    }
}
