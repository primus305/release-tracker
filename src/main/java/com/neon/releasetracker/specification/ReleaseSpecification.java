package com.neon.releasetracker.specification;

import com.neon.releasetracker.model.Release;
import com.neon.releasetracker.model.ReleaseStatus;
import com.neon.releasetracker.request.ReleaseSearchRequest;
import jakarta.persistence.criteria.Path;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Set;

public class ReleaseSpecification {

    public static Specification<Release> build(ReleaseSearchRequest request) {
        return Specification.where(nameContains(request.name()))
                .and(descriptionContains(request.description()))
                .and(hasStatuses(request.statuses()))
                .and(releaseDateBetween(request.releaseDateFrom(), request.releaseDateTo()));
    }

    private static Specification<Release> nameContains(String name) {
        return (root, query, cb) -> {
            if (StringUtils.isNotBlank(name)) {
                return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
            }
            return null;
        };
    }

    private static Specification<Release> descriptionContains(String description) {
        return (root, query, cb) -> {
            if (StringUtils.isNotBlank(description)) {
                return cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
            }
            return null;
        };
    }

    private static Specification<Release> hasStatuses(Set<ReleaseStatus> statuses) {
        return (root, query, cb) -> {
            if (statuses == null || statuses.isEmpty()) {
                return null;
            }
            return root.get("status").in(statuses);
        };
    }

    private static Specification<Release> releaseDateBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from == null && to == null) {
                return null;
            }

            Path<LocalDate> field = root.get("releaseDate");

            if (from != null && to != null) {
                return cb.between(field, from, to);
            }

            if (from != null) {
                return cb.greaterThanOrEqualTo(field, from);
            }

            return cb.lessThanOrEqualTo(field, to);
        };
    }

}
