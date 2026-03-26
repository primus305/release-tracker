package com.neon.releasetracker.request;

import com.neon.releasetracker.model.ReleaseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateReleaseRequest(@NotBlank(message = "Release name is required.")
                                   String name,
                                   String description,
                                   @NotNull(message = "Release status is required.")
                                   ReleaseStatus status,
                                   LocalDate releaseDate) {
}
