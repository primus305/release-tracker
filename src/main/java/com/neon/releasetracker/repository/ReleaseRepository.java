package com.neon.releasetracker.repository;

import com.neon.releasetracker.model.Release;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReleaseRepository extends JpaRepository<Release, Long> {
}
