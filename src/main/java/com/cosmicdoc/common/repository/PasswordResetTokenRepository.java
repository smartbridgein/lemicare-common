package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.PasswordResetToken;
import com.google.cloud.firestore.WriteBatch;

import java.util.Optional;

/**
 * Repository interface for managing PasswordResetToken entities in Firestore.
 */
public interface PasswordResetTokenRepository extends BaseRepository<PasswordResetToken, String> {

    /**
     * Finds a token by its value (which is also its document ID).
     *
     * @param token The token string to find.
     * @return An Optional containing the token if found, otherwise an empty Optional.
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Adds a delete operation for a token to an existing WriteBatch.
     *
     * @param batch The Firestore WriteBatch to add the operation to.
     * @param token The token string (document ID) to delete.
     */
    void deleteInTransaction(WriteBatch batch, String token);
}