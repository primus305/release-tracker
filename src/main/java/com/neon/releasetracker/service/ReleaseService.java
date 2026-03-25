package com.neon.releasetracker.service;

import com.neon.releasetracker.exception.ReleaseNotFoundException;
import com.neon.releasetracker.model.Release;
import com.neon.releasetracker.request.ReleaseRequest;
import com.neon.releasetracker.mapper.ReleaseMapper;
import com.neon.releasetracker.repository.ReleaseRepository;
import com.neon.releasetracker.response.ReleaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReleaseService {
    private final ReleaseRepository releaseRepository;
    private final ReleaseMapper releaseMapper;

    @Transactional
    public ReleaseResponse create(ReleaseRequest request) {
        Release release = releaseMapper.toEntity(request);
        Release savedRelease = releaseRepository.save(release);
        return releaseMapper.toResponse(savedRelease);
    }

    @Transactional
    public ReleaseResponse update(Long id, ReleaseRequest request) {
        Release release = releaseRepository.findById(id)
                .orElseThrow(() -> new ReleaseNotFoundException("Release with id " + id + " not found"));

        releaseMapper.updateEntity(request, release);

        return releaseMapper.toResponse(release);
    }

    @Transactional(readOnly = true)
    public ReleaseResponse getById(Long id) {
        return releaseRepository.findById(id)
                .map(releaseMapper::toResponse)
                .orElseThrow(() -> new ReleaseNotFoundException("Release with id " + id + " not found"));
    }

    @Transactional
    public void deleteById(Long id) {
        Release release = releaseRepository.findById(id)
                .orElseThrow(() -> new ReleaseNotFoundException("Release with id " + id + " not found"));

        releaseRepository.deleteById(release.getId());
    }
}
