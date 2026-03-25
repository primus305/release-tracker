package com.neon.releasetracker.service;

import com.neon.releasetracker.exception.ReleaseNotFoundException;
import com.neon.releasetracker.model.Release;
import com.neon.releasetracker.request.CreateReleaseRequest;
import com.neon.releasetracker.mapper.ReleaseMapper;
import com.neon.releasetracker.repository.ReleaseRepository;
import com.neon.releasetracker.request.UpdateReleaseRequest;
import com.neon.releasetracker.response.ReleaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Release release = getReleaseOrThrow(id);

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

        Release release = getReleaseOrThrow(id);
        releaseRepository.delete(release);

        log.info("Release deleted with id={}", id);
    }

    private Release getReleaseOrThrow(Long id) {
        return releaseRepository.findById(id)
                .orElseThrow(() -> new ReleaseNotFoundException(id));
    }
}
