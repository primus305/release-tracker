package com.neon.releasetracker.service;

import com.neon.releasetracker.data.ReleaseTestData;
import com.neon.releasetracker.exception.ReleaseNotFoundException;
import com.neon.releasetracker.mapper.ReleaseMapper;
import com.neon.releasetracker.model.Release;
import com.neon.releasetracker.repository.ReleaseRepository;
import com.neon.releasetracker.request.CreateReleaseRequest;
import com.neon.releasetracker.request.ReleaseSearchRequest;
import com.neon.releasetracker.request.UpdateReleaseRequest;
import com.neon.releasetracker.response.ReleaseResponse;
import com.neon.releasetracker.response.SearchResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ReleaseServiceUnitTest {

    @Mock
    private ReleaseRepository releaseRepository;

    @Mock
    private ReleaseMapper releaseMapper;

    @InjectMocks
    private ReleaseService releaseService;

    @Test
    void givenValidRequest_whenCreate_thenReturnResponse() {
        CreateReleaseRequest request = ReleaseTestData.createReleaseRequest().build();
        Release release = ReleaseTestData.release();
        ReleaseResponse expectedResponse = ReleaseTestData.releaseResponse();

        given(releaseMapper.toEntity(request)).willReturn(release);
        given(releaseRepository.save(release)).willReturn(release);
        given(releaseMapper.toResponse(release)).willReturn(expectedResponse);

        ReleaseResponse actualResponse = releaseService.create(request);

        assertThat(actualResponse)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(releaseRepository, times(1)).save(release);
        verify(releaseMapper, times(1)).toEntity(request);
        verify(releaseMapper, times(1)).toResponse(release);
        verifyNoMoreInteractions(releaseRepository, releaseMapper);
    }

    @Test
    void givenValidRequest_whenUpdate_thenReturnUpdatedResponse() {
        Long id = 1L;
        UpdateReleaseRequest updateRequest = ReleaseTestData.updateReleaseRequest().build();
        Release existingRelease = ReleaseTestData.release();
        ReleaseResponse expectedResponse = ReleaseTestData.releaseResponse();

        given(releaseRepository.findById(id)).willReturn(Optional.of(existingRelease));
        willDoNothing().given(releaseMapper).updateEntity(eq(updateRequest), eq(existingRelease));
        given(releaseMapper.toResponse(existingRelease)).willReturn(expectedResponse);

        ReleaseResponse actualResponse = releaseService.update(id, updateRequest);

        assertThat(actualResponse)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(releaseRepository, times(1)).findById(id);
        verify(releaseMapper,  times(1)).updateEntity(updateRequest, existingRelease);
        verify(releaseMapper, times(1)).toResponse(existingRelease);
        verifyNoMoreInteractions(releaseRepository, releaseMapper);
    }

    @Test
    void givenNonExistingId_whenUpdate_thenThrowException() {
        Long id = 1L;
        UpdateReleaseRequest updateRequest = ReleaseTestData.updateReleaseRequest().build();

        given(releaseRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> releaseService.update(id, updateRequest))
                .isInstanceOf(ReleaseNotFoundException.class);
    }

    @Test
    void givenExistingId_whenGetById_thenReturnResponse() {
        Long id = 1L;
        Release release = ReleaseTestData.release();
        ReleaseResponse expectedResponse = ReleaseTestData.releaseResponse();

        given(releaseRepository.findById(id)).willReturn(Optional.of(release));
        given(releaseMapper.toResponse(release)).willReturn(expectedResponse);

        ReleaseResponse actualResponse = releaseService.getById(id);

        assertThat(actualResponse)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);

        verify(releaseRepository, times(1)).findById(id);
        verify(releaseMapper, times(1)).toResponse(release);
        verifyNoMoreInteractions(releaseRepository);
    }

    @Test
    void givenNonExistingId_whenGetById_thenThrowException() {
        Long id = 1L;
        given(releaseRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> releaseService.getById(id))
                .isInstanceOf(ReleaseNotFoundException.class);
    }

    @Test
    void givenExistingId_whenDelete_thenSuccess() {
        Long id = 1L;
        Release release = ReleaseTestData.release();

        given(releaseRepository.findById(id)).willReturn(Optional.of(release));

        releaseService.deleteById(id);

        verify(releaseRepository, times(1)).findById(id);
        verify(releaseRepository, times(1)).delete(release);
        verifyNoMoreInteractions(releaseRepository);
    }

    @Test
    void givenNonExistingId_whenDelete_thenThrowException() {
        Long id = 1L;
        given(releaseRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> releaseService.deleteById(id))
                .isInstanceOf(ReleaseNotFoundException.class);

        verify(releaseRepository, times(1)).findById(id);
        verifyNoMoreInteractions(releaseRepository);
    }

    @Test
    void givenRequest_whenSearch_thenMapToSearchResponse() {
        ReleaseSearchRequest request = ReleaseTestData.releaseSearchRequest().build();
        Pageable pageable = PageRequest.of(0, 10);

        Release release = ReleaseTestData.release();
        Page<Release> page = new PageImpl<>(List.of(release), pageable, 1);

        given(releaseRepository.findAll(any(Specification.class), eq(pageable))).willReturn(page);
        given(releaseMapper.toResponse(any())).willReturn(ReleaseTestData.releaseResponse());

        SearchResponse<ReleaseResponse> response = releaseService.search(request, pageable);

        assertThat(response.content()).hasSize(1);
        assertThat(response.totalElements()).isEqualTo(1);
        assertThat(response.page()).isEqualTo(0);
    }
}