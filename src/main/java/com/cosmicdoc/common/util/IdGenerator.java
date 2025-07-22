package com.cosmicdoc.common.util;

import com.github.f4b6a3.ulid.UlidCreator;

/**
 * A utility class for generating unique, time-sortable identifiers (ULIDs).
 * This is the recommended approach for generating primary keys in a distributed system.
 */
public final class IdGenerator {

    private IdGenerator() {}

    /**
     * Generates a new, unique, time-sortable ULID.
     * @return A 26-character ULID string.
     */
    public static String newUlid() {
        return UlidCreator.getUlid().toString();
    }

    /**
     * Generates a new ID with a human-readable prefix.
     * Example: "purchase_01H6JW6D2C4D3ZJ7J1F0E0Q6Z0"
     *
     * @param prefix The prefix for the ID (e.g., "purchase", "sale").
     * @return A prefixed ID string.
     */
    public static String newId(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return newUlid();
        }
        return prefix.trim() + "_" + newUlid();
    }
}