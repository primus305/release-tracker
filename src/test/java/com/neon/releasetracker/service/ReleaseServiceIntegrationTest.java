package com.neon.releasetracker.service;

import com.neon.releasetracker.AbstractReleaseTrackerTest;
import com.neon.releasetracker.data.ReleaseTestData;
import com.neon.releasetracker.exception.ReleaseNotFoundException;
import com.neon.releasetracker.model.ReleaseStatus;
import com.neon.releasetracker.request.CreateReleaseRequest;
import com.neon.releasetracker.request.ReleaseSearchRequest;
import com.neon.releasetracker.request.UpdateReleaseRequest;
import com.neon.releasetracker.response.ReleaseResponse;
import com.neon.releasetracker.response.SearchResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;


import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ReleaseServiceIntegrationTest extends AbstractReleaseTrackerTest {

    @Autowired
    private ReleaseService releaseService;

    @Test
    void givenValidRequest_whenCreate_thenReturnResponse() {
        CreateReleaseRequest request = ReleaseTestData.createReleaseRequest().build();

        ReleaseResponse actualResponse = releaseService.create(request);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.id()).isNotNull();
        assertThat(actualResponse.name()).isEqualTo(request.name());
        assertThat(actualResponse.description()).isEqualTo(request.description());
        assertThat(actualResponse.status()).isEqualTo(ReleaseStatus.CREATED);
    }

    @Test
    void givenValidRequest_whenUpdate_thenReturnUpdatedResponse() {
        CreateReleaseRequest request = ReleaseTestData.createReleaseRequest().build();

        ReleaseResponse createReleaseResponse = releaseService.create(request);

        UpdateReleaseRequest updateRequest = ReleaseTestData.updateReleaseRequest().build();

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
        UpdateReleaseRequest updateRequest = ReleaseTestData.updateReleaseRequest().build();

        assertThatThrownBy(() -> releaseService.update(id, updateRequest))
                .isInstanceOf(ReleaseNotFoundException.class);
    }

    @Test
    void givenExistingId_whenGetById_thenReturnResponse() {
        CreateReleaseRequest request = ReleaseTestData.createReleaseRequest().build();

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
        CreateReleaseRequest request = ReleaseTestData.createReleaseRequest().build();

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

    @Test
    void givenNameFilter_whenSearch_thenReturnMatching() {
        CreateReleaseRequest request = ReleaseTestData.createReleaseRequest().build();

        ReleaseResponse createReleaseResponse = releaseService.create(request);

        ReleaseSearchRequest searchRequest = ReleaseTestData.releaseSearchRequest().build();

        SearchResponse<ReleaseResponse> response =
                releaseService.search(searchRequest, PageRequest.of(0, 10));

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().getFirst().name()).isEqualTo(createReleaseResponse.name());
    }

    @Test
    void givenMultipleStatuses_whenSearch_thenReturnMatching() {
        CreateReleaseRequest createReleaseRequest1 = ReleaseTestData.createReleaseRequest().build();
        ReleaseResponse createReleaseResponse1 = releaseService.create(createReleaseRequest1);

        UpdateReleaseRequest updateReleaseRequest = ReleaseTestData.updateReleaseRequest().build();
        releaseService.update(createReleaseResponse1.id(), updateReleaseRequest);

        CreateReleaseRequest createReleaseRequest2 = ReleaseTestData.createReleaseRequest()
                .name("Test Release 2")
                .description("Test Release description 2")
                .build();
        releaseService.create(createReleaseRequest2);

        ReleaseSearchRequest searchRequest = ReleaseSearchRequest.builder()
                .statuses(Set.of(ReleaseStatus.CREATED, ReleaseStatus.IN_DEVELOPMENT))
                .build();

        SearchResponse<ReleaseResponse> response = releaseService.search(searchRequest, PageRequest.of(0, 10));

        assertThat(response.content()).hasSize(2);
        assertThat(response.content())
                .extracting(ReleaseResponse::status)
                .containsExactlyInAnyOrder(
                        ReleaseStatus.CREATED,
                        ReleaseStatus.IN_DEVELOPMENT
                );
    }

    @Test
    void givenDateRange_whenSearch_thenReturnMatching() {
        CreateReleaseRequest createReleaseRequest1 = ReleaseTestData.createReleaseRequest().build();
        releaseService.create(createReleaseRequest1);

        CreateReleaseRequest createReleaseRequest2 = ReleaseTestData.createReleaseRequest()
                .name("Test Release 2")
                .description("Test Release description 2")
                .releaseDate(LocalDate.of(2026, 4, 25))
                .build();
        releaseService.create(createReleaseRequest2);

        ReleaseSearchRequest searchRequest = ReleaseSearchRequest.builder()
                .releaseDateFrom(LocalDate.of(2026, 1, 25))
                .releaseDateTo(LocalDate.of(2026, 3, 25))
                .build();

        SearchResponse<ReleaseResponse> response = releaseService.search(searchRequest, PageRequest.of(0, 10));

        assertThat(response.content()).hasSize(1);
    }

    @Test
    void givenCombinedFilters_whenSearch_thenReturnCorrect() {
        CreateReleaseRequest createReleaseRequest1 = ReleaseTestData.createReleaseRequest().build();
        releaseService.create(createReleaseRequest1);

        CreateReleaseRequest createReleaseRequest2 = ReleaseTestData.createReleaseRequest()
                .name("Alpha")
                .description("Backend release")
                .releaseDate(LocalDate.of(2026, 4, 25))
                .build();
        releaseService.create(createReleaseRequest2);

        ReleaseSearchRequest request = ReleaseTestData.releaseSearchRequest()
                .statuses(Set.of(ReleaseStatus.CREATED))
                .build();

        SearchResponse<ReleaseResponse> response =
                releaseService.search(request, PageRequest.of(0, 10));

        assertThat(response.content()).hasSize(1);
        assertThat(response.content())
                .extracting(ReleaseResponse::status)
                .containsExactlyInAnyOrder(ReleaseStatus.CREATED);
    }

    @Test
    void givenPagination_whenSearch_thenReturnPagedResult() {
        CreateReleaseRequest createReleaseRequest1 = ReleaseTestData.createReleaseRequest().build();
        releaseService.create(createReleaseRequest1);

        CreateReleaseRequest createReleaseRequest2 = ReleaseTestData.createReleaseRequest()
                .name("Test Release 2")
                .description("Test Release description 2")
                .releaseDate(LocalDate.of(2026, 4, 25))
                .build();
        releaseService.create(createReleaseRequest2);

        ReleaseSearchRequest request = ReleaseSearchRequest.builder().build();

        SearchResponse<ReleaseResponse> response =
                releaseService.search(request, PageRequest.of(0, 1));

        assertThat(response.content()).hasSize(1);
        assertThat(response.totalElements()).isEqualTo(2);
    }
}
