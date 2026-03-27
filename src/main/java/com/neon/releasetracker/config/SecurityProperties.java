package com.neon.releasetracker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "release-tracker.security")
public record SecurityProperties(String jwtSecret, User user) {

    public record User(String username, String password) { }
}
