package com.neon.releasetracker.response;

import com.neon.releasetracker.model.ReleaseStatus;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ReleaseResponse(Long id,
                              String name,
                              String description,
                              ReleaseStatus status,
                              LocalDate releaseDate) {
}
