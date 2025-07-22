package com.MooneyB.common.exceptions;

public class ChallengeNotFoundException extends RuntimeException {
    public ChallengeNotFoundException(String message) {
        super(message);
    }
    public ChallengeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}