package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Identities;
import com.cosmicdoc.common.repository.IdentityRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

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

}