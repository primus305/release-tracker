package com.neon.releasetracker.response;

import lombok.Builder;

@Builder
public record AuthResponse(String token) {
}
