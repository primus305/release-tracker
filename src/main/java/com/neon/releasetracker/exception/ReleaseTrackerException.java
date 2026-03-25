package com.neon.releasetracker.exception;

import lombok.Getter;

@Getter
public class ReleaseTrackerException extends RuntimeException {
    protected String messageKey;
    protected Object[] args;

    protected ReleaseTrackerException(String messageKey, Object... args) {
        this.messageKey = messageKey;
        this.args = args;
    }

}
