package com.neon.releasetracker.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CreateReleaseRequest(@NotBlank(message = "{release.name.required}")
                                   String name,
                                   String description,
                                   LocalDate releaseDate) {
}
