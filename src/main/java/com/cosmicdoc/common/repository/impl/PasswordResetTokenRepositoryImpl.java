package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.PasswordResetToken;
import com.cosmicdoc.common.repository.PasswordResetTokenRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PasswordResetTokenRepositoryImpl extends BaseRepositoryImpl<PasswordResetToken, String> implements PasswordResetTokenRepository {

    public PasswordResetTokenRepositoryImpl(Firestore firestore) {
        super();
        this.firestore = firestore;
    }

    @Override
    protected CollectionReference getCollection() {
        return firestore.collection("password_reset_tokens");
    }

    /**
     * Since the token is the document ID, this is a highly efficient direct lookup.
     */
    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        // We can simply reuse the generic findById method from our BaseRepository.
        return findById(token);
    }

    @Override
    public void deleteInTransaction(WriteBatch batch, String token) {
        if (token == null || token.isBlank()) {
            // Prevent errors with invalid input.
            return;
        }
        var docRef = getCollection().document(token);
        batch.delete(docRef);
    }
}