package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.VerificationToken;
import com.cosmicdoc.common.repository.VerificationTokenRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class VerificationTokenRepositoryImpl extends BaseRepositoryImpl<VerificationToken, String> implements VerificationTokenRepository {

    public VerificationTokenRepositoryImpl(Firestore firestore) {
        super();
        this.firestore = firestore;
    }

    @Override
    protected CollectionReference getCollection() {
        return firestore.collection("verification_tokens");
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        // Since the token is the document ID, this is a direct and fast lookup.
        return findById(token);
    }

    @Override
    public void deleteInTransaction(WriteBatch batch, String token) {
        var docRef = getCollection().document(token);
        batch.delete(docRef);
    }

    @Override
    public void saveInTransaction(WriteBatch batch, VerificationToken verificationToken) {
        var docRef = getCollection().document(verificationToken.getUserId());
        batch.set(docRef, verificationToken);
    }
}
