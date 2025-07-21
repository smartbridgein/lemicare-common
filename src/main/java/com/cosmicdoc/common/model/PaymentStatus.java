package com.cosmicdoc.common.model;

public enum PaymentStatus {
    /**
     * The invoice has been created, but no payment has been received yet.
     */
    PENDING,

    /**
     * A partial payment has been made, but there is still an outstanding balance.
     */
    PARTIALLY_PAID,

    /**
     * The invoice has been paid in full.
     */
    PAID,

    /**
     * The invoice has been cancelled and is no longer valid.
     */
    CANCELLED
}