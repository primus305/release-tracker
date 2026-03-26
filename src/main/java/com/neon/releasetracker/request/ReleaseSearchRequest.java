package com.neon.releasetracker.request;

import com.neon.releasetracker.model.ReleaseStatus;
import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record ReleaseSearchRequest(String name,
                                   Set<ReleaseStatus> statuses,
                                   String description,
                                   LocalDate releaseDateFrom,
                                   LocalDate releaseDateTo) {

    @AssertTrue(message = "Release date from must be before release date to")
    public boolean isValidDateRange() {
        if (releaseDateFrom == null || releaseDateTo == null) {
            return true;
        }
        return !releaseDateFrom.isAfter(releaseDateTo);
    }

}
