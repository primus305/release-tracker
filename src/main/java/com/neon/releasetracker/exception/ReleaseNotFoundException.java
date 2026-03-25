package com.neon.releasetracker.exception;

public class ReleaseNotFoundException extends ReleaseTrackerException {
    private static final String MESSAGE_KEY = "release.not.found";
    public ReleaseNotFoundException(Long id) {
        super(MESSAGE_KEY, id);
    }
}
