package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Wishlist;
import com.cosmicdoc.common.repository.WishlistRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class WishlistRepositoryImpl implements WishlistRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "wishlists"; // Sub-collection name

    public WishlistRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    // Helper to get the organization-specific wishlist collection
    private CollectionReference getCollection(String organizationId) {
        return firestore.collection("organizations").document(organizationId).collection(COLLECTION_NAME);
    }

    @Override
    public Optional<Wishlist> findByOrganizationIdAndCustomerId(String organizationId, String customerId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getCollection(organizationId).document(customerId);
        DocumentSnapshot document = docRef.get().get();

        if (document.exists()) {
            return Optional.ofNullable(document.toObject(Wishlist.class));
        }
        return Optional.empty();
    }

    @Override
    public Wishlist save(Wishlist wishlist) throws ExecutionException, InterruptedException {
        if (wishlist.getOrganizationId() == null || wishlist.getCustomerId() == null) {
            throw new IllegalArgumentException("OrganizationId and PatientId are required for Wishlist.");
        }
        // Ensure the organizationId in the POJO matches the path for consistency/security rules
        if (!wishlist.getOrganizationId().equals(wishlist.getOrganizationId())) {
            // This check might be redundant if the orgId is passed and set correctly.
            // Can remove if confident in upstream logic.
            throw new IllegalArgumentException("Mismatch between wishlist organizationId and path organizationId.");
        }

        ApiFuture<WriteResult> future = getCollection(wishlist.getOrganizationId()).document(wishlist.getCustomerId()).set(wishlist);
        future.get(); // Wait for the write to complete
        return wishlist;
    }

    @Override
    public void delete(String organizationId, String customerId) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = getCollection(organizationId).document(customerId).delete();
        future.get(); // Wait for the delete to complete
    }
}
