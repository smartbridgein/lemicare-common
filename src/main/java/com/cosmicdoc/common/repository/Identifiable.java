package com.cosmicdoc.common.repository;

/**
 * Interface for entities that have a unique identifier used as their Firestore document ID.
 */
public interface Identifiable {
    String getId();
    void setId(String id); // Add setter for cases where ID is generated
}
