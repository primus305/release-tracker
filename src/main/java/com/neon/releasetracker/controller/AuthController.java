package com.neon.releasetracker.controller;

import com.neon.releasetracker.request.AuthRequest;
import com.neon.releasetracker.response.AuthResponse;
import com.neon.releasetracker.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "release-tracker.security.enabled", havingValue = "true")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login user")
    @ApiResponse(responseCode = "200", description = "User authenticated")
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }
}