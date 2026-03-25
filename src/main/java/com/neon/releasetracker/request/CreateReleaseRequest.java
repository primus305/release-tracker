package com.neon.releasetracker.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CreateReleaseRequest(@NotBlank(message = "Release name is required.")
                                   String name,
                                   String description,
                                   @FutureOrPresent(message = "Release date must be today or in future")
                                   LocalDate releaseDate) {
}
