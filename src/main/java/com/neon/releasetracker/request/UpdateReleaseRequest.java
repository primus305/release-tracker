package com.neon.releasetracker.request;

import com.neon.releasetracker.model.ReleaseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateReleaseRequest(@NotBlank(message = "{release.name.required}")
                                   String name,
                                   String description,
                                   @NotNull(message = "{release.status.required}")
                                   ReleaseStatus status,
                                   LocalDate releaseDate) {
}
