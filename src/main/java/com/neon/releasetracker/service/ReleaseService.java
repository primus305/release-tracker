package com.neon.releasetracker.service;

import com.neon.releasetracker.exception.ReleaseNotFoundException;
import com.neon.releasetracker.model.Release;
import com.neon.releasetracker.request.CreateReleaseRequest;
import com.neon.releasetracker.mapper.ReleaseMapper;
import com.neon.releasetracker.repository.ReleaseRepository;
import com.neon.releasetracker.request.ReleaseSearchRequest;
import com.neon.releasetracker.request.UpdateReleaseRequest;
import com.neon.releasetracker.response.ReleaseResponse;
import com.neon.releasetracker.response.SearchResponse;
import com.neon.releasetracker.specification.ReleaseSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReleaseService {
    private final ReleaseRepository releaseRepository;
    private final ReleaseMapper releaseMapper;

    @Transactional
    public ReleaseResponse create(CreateReleaseRequest request) {
        log.info("Creating release with name={}", request.name());

        Release release = releaseMapper.toEntity(request);
        Release savedRelease = releaseRepository.save(release);

        log.info("Release created with id={}", savedRelease.getId());
        return releaseMapper.toResponse(savedRelease);
    }

    @Transactional
    public ReleaseResponse update(Long id, UpdateReleaseRequest request) {
        log.info("Updating release with id={}", id);

        Release release = getRelease(id);

        releaseMapper.updateEntity(request, release);

        log.info("Release updated with id={}", release.getId());
        return releaseMapper.toResponse(release);
    }

    @Transactional(readOnly = true)
    public ReleaseResponse getById(Long id) {
        log.info("Fetching release with id={}", id);

        return releaseRepository.findById(id)
                .map(releaseMapper::toResponse)
                .orElseThrow(() -> new ReleaseNotFoundException(id));
    }

    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting release with id={}", id);

        Release release = getRelease(id);
        releaseRepository.delete(release);

        log.info("Release deleted with id={}", id);
    }

    @Transactional(readOnly = true)
    public SearchResponse<ReleaseResponse> search(ReleaseSearchRequest request, Pageable pageable) {
        Page<Release> page = releaseRepository.findAll(ReleaseSpecification.build(request), pageable);

        List<ReleaseResponse> releases = page
                .stream()
                .map(releaseMapper::toResponse)
                .toList();

        return SearchResponse.<ReleaseResponse>builder()
                .content(releases)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .build();
    }

    private Release getRelease(Long id) {
        return releaseRepository.findById(id)
                .orElseThrow(() -> new ReleaseNotFoundException(id));
    }
}
