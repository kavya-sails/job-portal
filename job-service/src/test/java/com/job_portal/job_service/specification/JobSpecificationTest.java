package com.job_portal.job_service.specification;

import com.job_portal.job_service.dto.query.JobSearchCriteria;
import com.job_portal.job_service.entity.JobEntity;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes", "unchecked"})
class JobSpecificationTest {

    @Test
    void withFilters_titleAndCompanyAndLocation_callsCriteriaBuilderMethods() {
        // mocks
        Root<JobEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        // create Path mocks
        Path<String> titlePath = mock(Path.class);
        Path<String> locationPath = mock(Path.class);
        Path<Integer> expPath = mock(Path.class);
        Path<String> companyPath = mock(Path.class);
        Path<Object> expiresAtPath = mock(Path.class);

        // lower(expression) returns expression (mock Expression)
        Expression<String> lowerExpr = mock(Expression.class);
        Predicate likePred = mock(Predicate.class);
        Predicate eqPred = mock(Predicate.class);
        Predicate expPred = mock(Predicate.class);
        Predicate orPred = mock(Predicate.class);

        when(root.<String>get("title")).thenReturn(titlePath);
        when(root.<String>get("location")).thenReturn(locationPath);
        when(root.<Integer>get("experienceRequired")).thenReturn(expPath);
        when(root.<String>get("companyName")).thenReturn(companyPath);
        when(root.get("expiresAt")).thenReturn(expiresAtPath);

        when(cb.lower((Expression) any())).thenReturn(lowerExpr);
        when(cb.like(eq(lowerExpr), anyString())).thenReturn(likePred);
        when(cb.equal((Expression) any(), any())).thenReturn(eqPred);

        // expiry predicates
        when(cb.isNull(eq(expiresAtPath))).thenReturn(expPred);
        when(cb.greaterThan(eq((Expression) expiresAtPath), any(Comparable.class))).thenReturn(orPred);
        when(cb.or(eq(expPred), eq(orPred))).thenReturn(mock(Predicate.class));
        when(cb.and(any(Predicate[].class))).thenReturn(mock(Predicate.class));

        JobSearchCriteria c = new JobSearchCriteria();
        c.setTitle("abc");
        c.setLocation("LOC");
        c.setExperienceRequired(5);
        c.setCompanyName("Comp");

        var spec = JobSpecification.withFilters(c);
        Predicate p = spec.toPredicate(root, query, cb);
        assertThat(p).isNotNull();

        // verify interactions — use atLeastOnce times for stability
        verify(root, atLeastOnce()).get("title");
        verify(root, atLeastOnce()).get("location");
        verify(root, atLeastOnce()).get("experienceRequired");
        verify(root, atLeastOnce()).get("companyName");
        // actual implementation calls expiresAt twice, so expect 2
        verify(root, times(2)).get("expiresAt");

        verify(cb, atLeastOnce()).lower(any(Expression.class));
        verify(cb).like(any(Expression.class), contains("abc"));
        verify(cb).equal(any(Expression.class), eq(5));
        verify(cb).or(any(Predicate.class), any(Predicate.class));
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    void withFilters_emptyCriteria_stillAddsExpiryPredicate() {
        Root<JobEntity> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Path<Object> expiresAtPath = mock(Path.class);

        when(root.get("expiresAt")).thenReturn(expiresAtPath);

        Predicate p1 = mock(Predicate.class);
        Predicate p2 = mock(Predicate.class);

        when(cb.isNull(eq(expiresAtPath))).thenReturn(p1);
        when(cb.greaterThan(eq((Expression) expiresAtPath), any(Comparable.class))).thenReturn(p2);
        when(cb.or(eq(p1), eq(p2))).thenReturn(mock(Predicate.class));
        when(cb.and(any(Predicate[].class))).thenReturn(mock(Predicate.class));

        JobSearchCriteria c = new JobSearchCriteria();
        var spec = JobSpecification.withFilters(c);
        Predicate p = spec.toPredicate(root, query, cb);
        assertThat(p).isNotNull();

        // implementation calls expiresAt twice — account for that
        verify(root, times(2)).get("expiresAt");
        verify(cb).or(p1, p2);
    }
}