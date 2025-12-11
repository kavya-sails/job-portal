package com.job_portal.job_service.specification;

import com.job_portal.job_service.dto.query.JobSearchCriteria;
import com.job_portal.job_service.entity.JobEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class JobSpecification {

    public static Specification<JobEntity> withFilters(JobSearchCriteria c) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (hasText(c.getTitle())) {
                String title = normalize(c.getTitle());
                predicates.add(
                        cb.like(cb.lower(root.get("title")), "%" + title + "%")
                );
            }

            if (hasText(c.getLocation())) {
                String location = normalize(c.getLocation());
                predicates.add(
                        cb.equal(cb.lower(root.get("location")), location)
                );
            }

            if (c.getExperienceRequired() != null) {
                predicates.add(
                        cb.equal(root.get("experienceRequired"), c.getExperienceRequired())
                );
            }

            if (hasText(c.getCompanyName())) {
                String company = normalize(c.getCompanyName());
                predicates.add(
                        cb.like(cb.lower(root.get("companyName")), "%" + company + "%")
                );
            }


            // ============================
            // Exclude Expired Jobs
            // ============================
            predicates.add(
                    cb.or(
                            cb.isNull(root.get("expiresAt")),
                            cb.greaterThan(root.get("expiresAt"), Instant.now())
                    )
            );

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String normalize(String value) {
        return value.trim().toLowerCase();
    }
}
