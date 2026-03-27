package com.neon.releasetracker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "release-tracker.security.user")
public record SecurityUserProperties(String username, String password) {
}
