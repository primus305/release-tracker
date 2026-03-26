package com.neon.releasetracker.response;

import lombok.Builder;

import java.util.List;

@Builder
public record SearchResponse<T>(List<T> content,
                                int page,
                                int size,
                                long totalElements) {
}
