package com.neon.releasetracker.service;

import com.neon.releasetracker.request.AuthRequest;
import com.neon.releasetracker.response.AuthResponse;
import com.neon.releasetracker.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "release-tracker.security.enabled", havingValue = "true")
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        String token = jwtTokenService.generateToken(request.username());

        return AuthResponse.builder()
                .token(token)
                .build();
    }
}
