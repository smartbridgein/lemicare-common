package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Identities;
import com.cosmicdoc.common.repository.IdentityRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
@Repository
public class IdentityRepositoryImpl extends AbstractTransactionalRepositoryImpl<Identities, String> implements IdentityRepository {

    public IdentityRepositoryImpl(Firestore firestore) {
        super(firestore); // Pass the entity class
        this.firestore = firestore;
    }

    @Override
    protected CollectionReference getCollection() {
        return firestore.collection("identities");
    }

    @Override
    public Optional<Identities> findByEmail(String email) {
        try {
            var query = getCollection().whereEqualTo("email", email);
            var querySnapshot = query.get().get();
            if (!querySnapshot.getDocuments().isEmpty()) {
                return Optional.ofNullable(querySnapshot.getDocuments().get(0).toObject(Identities.class));
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error finding identity by email", e);
        }
    }

    @Override
    public Optional<Identities> findByLinkedUserId(String userId) {
        try {
            var query = getCollection().whereEqualTo("linkedUserId", userId);
            var querySnapshot = query.get().get();
            if (!querySnapshot.getDocuments().isEmpty()) {
                return Optional.ofNullable(querySnapshot.getDocuments().get(0).toObject(Identities.class));
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error finding identity by linked user ID", e);
        }
    }

    @Override
    public Optional<Identities> findByLinkedCustomerId(String customerId) {
        try {
            var query = getCollection().whereEqualTo("linkedCustomerId", customerId);
            var querySnapshot = query.get().get();
            if (!querySnapshot.getDocuments().isEmpty()) {
                return Optional.ofNullable(querySnapshot.getDocuments().get(0).toObject(Identities.class));
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error finding identity by linked customer ID", e);
        }
    }


    public void updateLastLogin(String identityId) {
        try {
            var docRef = getCollection().document(identityId);
            docRef.update("lastLoginAt", new Timestamp(System.currentTimeMillis()));
        } catch (Exception e) {
            throw new RuntimeException("Error while updating last login for identity", e);
        }
    }


    /**
     * Finds an identity by its mobile number.
     * @param mobileNumber The mobile number to search for.
     * @return An Optional containing the found Identity, or empty if not found.
     * @throws RuntimeException if an underlying Firestore operation fails.
     */
    @Override
    public Optional<Identities> findByMobileNumber(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.isBlank()) {
            log.debug("Attempted to find identity by null or blank mobile number.");
            return Optional.empty();
        }
        try {
            QuerySnapshot querySnapshot = getCollection()
                    .whereEqualTo("mobileNumber", mobileNumber)
                    .limit(1) // Assuming mobile number is unique for identities
                    .get()
                    .get();

            if (!querySnapshot.getDocuments().isEmpty()) {
                return Optional.ofNullable(querySnapshot.getDocuments().get(0).toObject(Identities.class));
            }
            return Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Identity lookup by mobile number interrupted for '{}'", mobileNumber, e);
            throw new RuntimeException("Identity lookup by mobile number was interrupted.", e);
        } catch (ExecutionException e) {
            log.error("Failed to find identity by mobile number '{}'", mobileNumber, e);
            throw new RuntimeException("Failed to find identity by mobile number due to Firestore error.", e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while finding identity by mobile number '{}'", mobileNumber, e);
            throw new RuntimeException("Unexpected error during identity lookup by mobile number.", e);
        }
    }

    /**
     * Finds an identity by either its email or mobile number.
     * Prioritizes email if both are provided and an identity is found by email.
     * @param email The email address to search for (can be null or blank).
     * @param mobileNumber The mobile number to search for (can be null or blank).
     * @return An Optional containing the found Identity, or empty if not found.
     * @throws RuntimeException if an underlying Firestore operation fails.
     */
    @Override
    public Optional<Identities> findByEmailOrMobileNumber(String email, String mobileNumber) {
        // First, try finding by email if available
        if (email != null && !email.isBlank()) {
            try {
                Optional<Identities> identityByEmail = findByEmail(email);
                if (identityByEmail.isPresent()) {
                    log.debug("Found identity by email '{}'.", email);
                    return identityByEmail;
                }
            } catch (RuntimeException e) {
                log.warn("Error when trying to find identity by email '{}' as part of findByEmailOrMobileNumber. Proceeding to check mobile number. Error: {}", email, e.getMessage());
                // Don't rethrow, attempt mobile number lookup
            }
        }

        // If not found by email, or email was not provided/had an error, try finding by mobile number
        if (mobileNumber != null && !mobileNumber.isBlank()) {
            try {
                Optional<Identities> identityByMobile = findByMobileNumber(mobileNumber);
                if (identityByMobile.isPresent()) {
                    log.debug("Found identity by mobile number '{}'.", mobileNumber);
                    return identityByMobile;
                }
            } catch (RuntimeException e) {
                log.warn("Error when trying to find identity by mobile number '{}' as part of findByEmailOrMobileNumber. Error: {}", mobileNumber, e.getMessage());
                // Don't rethrow, just return empty
            }
        }

        log.debug("No identity found by email '{}' or mobile number '{}'.",
                (email != null ? email : "N/A"), (mobileNumber != null ? mobileNumber : "N/A"));
        return Optional.empty();
    }
}
