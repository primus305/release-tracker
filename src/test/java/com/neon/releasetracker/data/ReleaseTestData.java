package com.neon.releasetracker.data;

import com.neon.releasetracker.model.Release;
import com.neon.releasetracker.model.ReleaseStatus;
import com.neon.releasetracker.request.CreateReleaseRequest;
import com.neon.releasetracker.request.ReleaseSearchRequest;
import com.neon.releasetracker.request.UpdateReleaseRequest;
import com.neon.releasetracker.response.ReleaseResponse;

import java.time.LocalDate;

public final class ReleaseTestData {

    private ReleaseTestData() {}

    public static CreateReleaseRequest.CreateReleaseRequestBuilder createReleaseRequest() {
        return CreateReleaseRequest.builder()
                .name("Test Release")
                .description("Test Release description")
                .releaseDate(LocalDate.of(2026, 2, 25));
    }

    public static Release release() {
        return new Release("Test Release", "Test Release description", LocalDate.of(2026, 2, 25));
    }

    public static ReleaseResponse releaseResponse() {
        return ReleaseResponse.builder()
                .id(1L)
                .name("Test Release")
                .description("Test Release description")
                .status(ReleaseStatus.CREATED)
                .releaseDate(LocalDate.of(2026, 2, 25))
                .build();
    }

    public static UpdateReleaseRequest.UpdateReleaseRequestBuilder updateReleaseRequest() {
        return UpdateReleaseRequest.builder()
                .name("Test Release update")
                .description("Test Release description update")
                .status(ReleaseStatus.IN_DEVELOPMENT)
                .releaseDate(LocalDate.of(2026, 4, 25));
    }

    public static ReleaseSearchRequest.ReleaseSearchRequestBuilder releaseSearchRequest() {
        return ReleaseSearchRequest.builder()
                .name("test");
    }
}
