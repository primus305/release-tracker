package com.neon.releasetracker.service;

import com.neon.releasetracker.AbstractReleaseTrackerTest;
import com.neon.releasetracker.data.ReleaseTestData;
import com.neon.releasetracker.exception.ReleaseNotFoundException;
import com.neon.releasetracker.model.ReleaseStatus;
import com.neon.releasetracker.request.CreateReleaseRequest;
import com.neon.releasetracker.request.UpdateReleaseRequest;
import com.neon.releasetracker.response.ReleaseResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ReleaseServiceIntegrationTest extends AbstractReleaseTrackerTest {

    @Autowired
    private ReleaseService releaseService;

    @Test
    void givenValidRequest_whenCreate_thenReturnResponse() {
        CreateReleaseRequest request = ReleaseTestData.createReleaseRequest();

        ReleaseResponse actualResponse = releaseService.create(request);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.id()).isNotNull();
        assertThat(actualResponse.name()).isEqualTo(request.name());
        assertThat(actualResponse.description()).isEqualTo(request.description());
        assertThat(actualResponse.status()).isEqualTo(ReleaseStatus.CREATED);
    }

    @Test
    void givenValidRequest_whenUpdate_thenReturnUpdatedResponse() {
        CreateReleaseRequest request = ReleaseTestData.createReleaseRequest();

        ReleaseResponse createReleaseResponse = releaseService.create(request);

        UpdateReleaseRequest updateRequest = ReleaseTestData.updateReleaseRequest();

        ReleaseResponse actualResponse = releaseService.update(createReleaseResponse.id(), updateRequest);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.id()).isEqualTo(createReleaseResponse.id());
        assertThat(actualResponse.name()).isEqualTo(updateRequest.name());
        assertThat(actualResponse.description()).isEqualTo(updateRequest.description());
        assertThat(actualResponse.status()).isEqualTo(updateRequest.status());
    }

    @Test
    void givenNonExistingId_whenUpdate_thenThrowException() {
        Long id = 1L;
        UpdateReleaseRequest updateRequest = ReleaseTestData.updateReleaseRequest();

        assertThatThrownBy(() -> releaseService.update(id, updateRequest))
                .isInstanceOf(ReleaseNotFoundException.class);
    }

    @Test
    void givenExistingId_whenGetById_thenReturnResponse() {
        CreateReleaseRequest request = ReleaseTestData.createReleaseRequest();

        ReleaseResponse createReleaseResponse = releaseService.create(request);

        ReleaseResponse actualResponse = releaseService.getById(createReleaseResponse.id());

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.id()).isEqualTo(createReleaseResponse.id());
        assertThat(actualResponse.name()).isEqualTo(request.name());
        assertThat(actualResponse.description()).isEqualTo(request.description());
        assertThat(actualResponse.status()).isEqualTo(createReleaseResponse.status());
    }

    @Test
    void givenNonExistingId_whenGetById_thenThrowException() {
        Long id = 1L;

        assertThatThrownBy(() -> releaseService.getById(id))
                .isInstanceOf(ReleaseNotFoundException.class);
    }

    @Test
    void givenExistingId_whenDelete_thenSuccess() {
        CreateReleaseRequest request = ReleaseTestData.createReleaseRequest();

        ReleaseResponse createReleaseResponse = releaseService.create(request);

        releaseService.deleteById(createReleaseResponse.id());

        assertThatThrownBy(() -> releaseService.getById(createReleaseResponse.id()))
                .isInstanceOf(ReleaseNotFoundException.class);
    }

    @Test
    void givenNonExistingId_whenDelete_thenThrowException() {
        Long id = 1L;

        assertThatThrownBy(() -> releaseService.deleteById(id))
                .isInstanceOf(ReleaseNotFoundException.class);
    }
}
