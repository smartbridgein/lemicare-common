package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Cart;
import com.cosmicdoc.common.repository.CartRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class CartRepositoryImpl implements CartRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "carts";

    public CartRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    public Optional<Cart> findByOrgIdAndUserId(String orgId, String userId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("orgId", orgId)
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "ACTIVE")
                .limit(1); // Assuming one active cart per user

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<Cart> carts = querySnapshot.get().getDocuments().stream()
                .map(doc -> doc.toObject(Cart.class))
                .collect(Collectors.toList());

        return carts.isEmpty() ? Optional.empty() : Optional.of(carts.get(0));
    }

    public Optional<Cart> findByOrgIdAndGuestId(String orgId, String guestId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("orgId", orgId)
                .whereEqualTo("guestId", guestId)
                .whereEqualTo("status", "ACTIVE")
                .limit(1); // Assuming one active cart per guest

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<Cart> carts = querySnapshot.get().getDocuments().stream()
                .map(doc -> doc.toObject(Cart.class))
                .collect(Collectors.toList());

        return carts.isEmpty() ? Optional.empty() : Optional.of(carts.get(0));
    }

    public Optional<Cart> findById(String cartId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(cartId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return Optional.of(document.toObject(Cart.class));
        }
        return Optional.empty();
    }

    public Cart save(Cart cart) throws ExecutionException, InterruptedException {
        if (cart.getCartId() == null) {
            DocumentReference newDocRef = firestore.collection(COLLECTION_NAME).document();
            cart.setCartId(newDocRef.getId());
            ApiFuture<WriteResult> future = newDocRef.set(cart);
            future.get(); // Wait for completion
            return cart;
        } else {
            ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(cart.getCartId()).set(cart);
            future.get(); // Wait for completion
            return cart;
        }
    }

    public void delete(String cartId) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(cartId).delete();
        future.get(); // Wait for completion
    }
}
