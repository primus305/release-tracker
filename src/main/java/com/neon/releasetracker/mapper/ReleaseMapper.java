package com.neon.releasetracker.mapper;

import com.neon.releasetracker.request.ReleaseRequest;
import com.neon.releasetracker.model.Release;
import com.neon.releasetracker.response.ReleaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReleaseMapper {

    Release toEntity(ReleaseRequest request);

    ReleaseResponse toResponse(Release release);

    void updateEntity(ReleaseRequest request, @MappingTarget Release entity);

}
