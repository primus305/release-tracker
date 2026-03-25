package com.neon.releasetracker.mapper;

import com.neon.releasetracker.request.CreateReleaseRequest;
import com.neon.releasetracker.model.Release;
import com.neon.releasetracker.request.UpdateReleaseRequest;
import com.neon.releasetracker.response.ReleaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReleaseMapper {

    Release toEntity(CreateReleaseRequest request);

    ReleaseResponse toResponse(Release release);

    void updateEntity(UpdateReleaseRequest request, @MappingTarget Release entity);

}
