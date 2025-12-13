package com.job_portal.job_service.specification;

import com.job_portal.job_service.dto.query.JobSearchCriteria;
import com.job_portal.job_service.entity.JobEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class JobSpecification {

    public static Specification<JobEntity> withFilters(JobSearchCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(c.getTitle())) {
                String title = c.getTitle().trim().toLowerCase();
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title + "%"));
            }

            if (StringUtils.hasText(c.getLocation())) {
                String location = c.getLocation().trim().toLowerCase();
                predicates.add(cb.equal(cb.lower(root.get("location")), location));
            }

            if (c.getExperienceRequired() != null) {
                predicates.add(cb.equal(root.get("experienceRequired"), c.getExperienceRequired()));
            }

            if (StringUtils.hasText(c.getCompanyName())) {
                String company = c.getCompanyName().trim().toLowerCase();
                predicates.add(cb.like(cb.lower(root.get("companyName")), "%" + company + "%"));
            }

            // Exclude expired jobs
            predicates.add(cb.or(
                    cb.isNull(root.get("expiresAt")),
                    cb.greaterThan(root.get("expiresAt"), Instant.now())
            ));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
