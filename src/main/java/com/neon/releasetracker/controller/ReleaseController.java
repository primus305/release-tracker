package com.neon.releasetracker.controller;

import com.neon.releasetracker.common.CommonRestControllerResponses;
import com.neon.releasetracker.request.ReleaseRequest;
import com.neon.releasetracker.response.ReleaseResponse;
import com.neon.releasetracker.service.ReleaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/releases")
@RequiredArgsConstructor
@CommonRestControllerResponses
public class ReleaseController {

    private final ReleaseService releaseService;

    @Operation(summary = "Create a new release.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Release created"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "409", description = "Conflict")
            })
    @PostMapping
    ResponseEntity<ReleaseResponse> create(@RequestBody @Valid ReleaseRequest request) {
        ReleaseResponse response = releaseService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Update the release.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Release updated"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "409", description = "Conflict")
            })
    @PutMapping(value = "/{id}")
    ResponseEntity<ReleaseResponse> update(@PathVariable Long id,
                                           @RequestBody @Valid ReleaseRequest request) {
        ReleaseResponse response = releaseService.update(id, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(summary = "Get release by id.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Release"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
            })
    @GetMapping(value = "/{id}")
    ResponseEntity<ReleaseResponse> getById(@PathVariable Long id) {
        ReleaseResponse response = releaseService.getById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(summary = "Delete release by id.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Release deleted"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
            })
    @DeleteMapping(value = "/{id}")
    ResponseEntity<Void> deleteById(@PathVariable Long id) {
        releaseService.deleteById(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
