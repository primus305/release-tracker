package com.neon.releasetracker.response;

import lombok.Builder;

@Builder
public record ErrorResponse(String errorCode,
                            int status,
                            String message) {
}
