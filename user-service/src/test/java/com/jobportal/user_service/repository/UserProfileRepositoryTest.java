package com.jobportal.user_service.repository;

import com.jobportal.user_service.entity.UserProfile;
import com.jobportal.user_service.enums.ExperienceLevel;
import com.jobportal.user_service.enums.JobRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserProfileRepositoryTest {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Test
    void save_and_findById_works() {
        UserProfile profile = UserProfile.builder()
                .id(1L) // you set id manually (maps to auth userId)
                .firstName("Pavan")
                .lastName("Kumar")
                .dob(LocalDate.of(2000, 1, 1))
                .phone("9876543210")
                .skills("Java,Spring")
                .experience(2)
                .jobRole(JobRole.BACKEND_DEVELOPER)
                .experienceLevel(ExperienceLevel.JUNIOR)
                .resumeUrl("https://example.com/resume.pdf") // NOT NULL
                .build();

        userProfileRepository.save(profile);

        var found = userProfileRepository.findById(1L);
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Pavan");
        assertThat(found.get().getResumeUrl()).isEqualTo("https://example.com/resume.pdf");
        assertThat(found.get().getCreatedAt()).isNotNull(); // set by @PrePersist
        assertThat(found.get().getUpdatedAt()).isNotNull();
    }

    @Test
    void existsById_returnsTrue_whenExists() {
        userProfileRepository.save(
                UserProfile.builder()
                        .id(2L)
                        .firstName("A")
                        .lastName("B")
                        .resumeUrl("https://example.com/r.pdf")
                        .build()
        );

        assertThat(userProfileRepository.existsById(2L)).isTrue();
        assertThat(userProfileRepository.existsById(999L)).isFalse();
    }
}
