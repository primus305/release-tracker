package com.neon.releasetracker.data;

import com.neon.releasetracker.model.Release;
import com.neon.releasetracker.model.ReleaseStatus;
import com.neon.releasetracker.request.CreateReleaseRequest;
import com.neon.releasetracker.request.UpdateReleaseRequest;
import com.neon.releasetracker.response.ReleaseResponse;

import java.time.LocalDate;

public final class ReleaseTestData {

    private ReleaseTestData() {}

    public static CreateReleaseRequest createReleaseRequest() {
        return CreateReleaseRequest.builder()
                .name("Test Release")
                .description("Test Release description")
                .releaseDate(LocalDate.now().plusMonths(1))
                .build();
    }

    public static Release createRelease() {
        return new Release("Test Release", "Test Release description", LocalDate.now().plusMonths(1));
    }

    public static ReleaseResponse createReleaseResponse() {
        return ReleaseResponse.builder()
                .id(1L)
                .name("Test Release")
                .description("Test Release description")
                .status(ReleaseStatus.CREATED)
                .releaseDate(LocalDate.now().plusMonths(1))
                .build();
    }

    public static UpdateReleaseRequest updateReleaseRequest() {
        return UpdateReleaseRequest.builder()
                .name("Test Release update")
                .description("Test Release description update")
                .releaseDate(LocalDate.now().plusMonths(3))
                .build();
    }
}
