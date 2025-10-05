package com.cosmicdoc.common.model;

public enum VerificationTokenType {
    ACCOUNT_ACTIVATION, // For initial signup verification
    PASSWORD_RESET,     // For password reset flows
    EMAIL_CHANGE        // If you implement email address change verification
}