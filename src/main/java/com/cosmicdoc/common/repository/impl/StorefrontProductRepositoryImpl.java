package com.cosmicdoc.common.repository.impl;


import com.cosmicdoc.common.model.StorefrontProduct;
import com.cosmicdoc.common.repository.StorefrontProductRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository

public class StorefrontProductRepositoryImpl implements StorefrontProductRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "storefront_products";

    public StorefrontProductRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    private CollectionReference getCollection(String organizationId, String branchId) {
        return firestore.collection("organizations").document(organizationId)
                .collection("branches").document(branchId)
                .collection(COLLECTION_NAME);
    }

    @Override
    public StorefrontProduct save(StorefrontProduct product,String organizationId, String branchId) {
        try {
            getCollection(organizationId, branchId)
                    .document(product.getProductId()).set(product).get();
            return product;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error saving storefront product", e);
        }
    }

    @Override
    public Optional<StorefrontProduct> findById(String organizationId, String branchId, String productId) {
        try {
            var doc = getCollection(organizationId, branchId).document(productId).get().get();
            return doc.exists() ? Optional.ofNullable(doc.toObject(StorefrontProduct.class)) : Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding storefront product by ID", e);
        }
    }

    @Override
    public List<StorefrontProduct> findAllVisibleByBranch(String organizationId, String branchId) {
        try {
            Query query = getCollection(organizationId, branchId)
                    .whereEqualTo("isVisible", true); // Only fetch products marked as visible

            return query.get().get().getDocuments().stream()
                    .map(doc -> doc.toObject(StorefrontProduct.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error fetching visible products for branch", e);
        }
    }

    @Override
    public List<StorefrontProduct> findAllVisibleByCategory(String organizationId, String categoryId) {
        // This is a Collection Group query. It finds products in a category across ALL branches.
        try {
            Query query = firestore.collectionGroup(COLLECTION_NAME)
                    .whereEqualTo("organizationId", organizationId)
                    .whereEqualTo("categoryId", categoryId)
                    .whereEqualTo("isVisible", true);

            return query.get().get().getDocuments().stream()
                    .map(doc -> doc.toObject(StorefrontProduct.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error fetching visible products by category", e);
        }
    }
}