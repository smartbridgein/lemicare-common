package com.cosmicdoc.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum IdentityType {
    CUSTOMER("CUSTOMER", "Represents an external customer account."),
    ADMIN("ADMIN", "Represents an internal administrator account."),
    EMPLOYEE("EMPLOYEE", "Represents an internal employee account."),
    SYSTEM("SYSTEM", "Represents an automated system or service account."),
    VIPCUSTOMER("VIPCUSTOMER", "Represents an external customer account."),
    SALESPERSON("SALESPERSON","Represents an internal salesperson account");


    private final String value;
    private final String description;

    IdentityType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    // This annotation tells Jackson how to serialize the enum to JSON
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Custom deserializer for Jackson to map string values back to enum instances.
     * This allows case-insensitive matching or mapping from an alternative value.
     * @param value The string value to convert.
     * @return The corresponding IdentityType enum.
     * @throws IllegalArgumentException if no matching enum is found.
     */
    @JsonCreator
    public static IdentityType fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("IdentityType value cannot be null or blank.");
        }
        return Arrays.stream(IdentityType.values())
                .filter(type -> type.value.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown IdentityType value: " + value));
    }

    /**
     * Provides a safe way to get an IdentityType from a string without throwing an exception.
     * @param value The string value to convert.
     * @return An Optional containing the IdentityType, or empty if not found.
     */
    public static Optional<IdentityType> safeFromValue(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Arrays.stream(IdentityType.values())
                .filter(type -> type.value.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value))
                .findFirst();
    }
}
