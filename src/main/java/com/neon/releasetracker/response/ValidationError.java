package com.neon.releasetracker.response;

import lombok.Builder;

@Builder
public record ValidationError(String field, String message) {
}
