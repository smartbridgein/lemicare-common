package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.CartItem;
import com.cosmicdoc.common.repository.CartItemRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class CartItemRepositoryImpl implements CartItemRepository {
    private final Firestore firestore;
    private static final String COLLECTION_NAME = "cartItems";

    public CartItemRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    public List<CartItem> findByCartId(String cartId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("cartId", cartId);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        return querySnapshot.get().getDocuments().stream()
                .map(doc -> doc.toObject(CartItem.class))
                .collect(Collectors.toList());
    }

    public Optional<CartItem> findByCartIdAndProductId(String cartId, String productId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("cartId", cartId)
                .whereEqualTo("productId", productId)
                .limit(1);

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<CartItem> items = querySnapshot.get().getDocuments().stream()
                .map(doc -> doc.toObject(CartItem.class))
                .collect(Collectors.toList());

        return items.isEmpty() ? Optional.empty() : Optional.of(items.get(0));
    }

    public Optional<CartItem> findById(String cartItemId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("cartItemId", cartItemId)
                .limit(1)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        if (!documents.isEmpty()) {
            return Optional.of(documents.get(0).toObject(CartItem.class));
        }
        return Optional.empty();
    }

    public CartItem save(CartItem cartItem) throws ExecutionException, InterruptedException {
        if (cartItem.getCartItemId() == null) {
            DocumentReference newDocRef = firestore.collection(COLLECTION_NAME).document();
            cartItem.setCartItemId(newDocRef.getId());
            ApiFuture<WriteResult> future = newDocRef.set(cartItem);
            future.get();
            return cartItem;
        } else {
            ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(cartItem.getCartItemId()).set(cartItem);
            future.get();
            return cartItem;
        }
    }

    public void delete(String cartItemId) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME).document(cartItemId).delete();
        future.get();
    }

    public void deleteAllByCartId(String cartId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME).whereEqualTo("cartId", cartId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            document.getReference().delete();
        }
    }
}

