package com.neon.releasetracker.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ErrorResponse(String errorCode,
                            int status,
                            String message,
                            List<ValidationError> errors) {
}
