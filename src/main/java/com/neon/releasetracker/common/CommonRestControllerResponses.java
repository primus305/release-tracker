package com.neon.releasetracker.common;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
@ApiResponse(responseCode = "401", description = "Unauthorized")
@ApiResponse(responseCode = "403", description = "Forbidden")
@ApiResponse(responseCode = "500", description = "Internal server error")
public @interface CommonRestControllerResponses {}
