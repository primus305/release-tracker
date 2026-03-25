package com.neon.releasetracker.controller;

import com.neon.releasetracker.common.CommonRestControllerResponses;
import com.neon.releasetracker.request.CreateReleaseRequest;
import com.neon.releasetracker.request.UpdateReleaseRequest;
import com.neon.releasetracker.response.ErrorResponse;
import com.neon.releasetracker.response.ReleaseResponse;
import com.neon.releasetracker.service.ReleaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/releases")
@RequiredArgsConstructor
@CommonRestControllerResponses
public class ReleaseController {

    private final ReleaseService releaseService;

    @Operation(summary = "Create a new release.")
    @ApiResponse(responseCode = "201", description = "Release created")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ReleaseResponse create(@RequestBody @Valid CreateReleaseRequest request) {
        return releaseService.create(request);
    }

    @Operation(summary = "Update the release.")
    @ApiResponse(responseCode = "200", description = "Release updated")
    @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))
            })
    @PutMapping(value = "/{id}")
    ReleaseResponse update(@PathVariable @Positive Long id,
                           @RequestBody @Valid UpdateReleaseRequest request) {
        return releaseService.update(id, request);
    }

    @Operation(summary = "Get release by id.")
    @ApiResponse(responseCode = "200", description = "Release retrieved")
    @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))
            })
    @GetMapping(value = "/{id}")
    ReleaseResponse getById(@PathVariable @Positive Long id) {
        return releaseService.getById(id);
    }

    @Operation(summary = "Delete release by id.")
    @ApiResponse(responseCode = "204", description = "Release deleted")
    @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))
            })
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteById(@PathVariable @Positive Long id) {
        releaseService.deleteById(id);
    }
}
