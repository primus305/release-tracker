package com.neon.releasetracker.request;

import lombok.Builder;

@Builder
public record AuthRequest(String username, String password) {
}
