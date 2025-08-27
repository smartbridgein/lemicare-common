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
@RequiredArgsConstructor
public class StorefrontProductRepositoryImpl implements StorefrontProductRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "storefront_products";

    /**
     * CORRECTED: Helper now gets the sub-collection directly under the organization.
     */
    private CollectionReference getCollection(String organizationId) {
        return firestore.collection("organizations").document(organizationId).collection(COLLECTION_NAME);
    }

    @Override
    public StorefrontProduct save(StorefrontProduct product) {
        if (product.getOrganizationId() == null || product.getProductId() == null) {
            throw new IllegalArgumentException("OrganizationId and ProductId are required.");
        }
        try {
            getCollection(product.getOrganizationId()).document(product.getProductId()).set(product).get();
            return product;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error saving storefront product", e);
        }
    }

    /**
     * CORRECTED: Renamed findByOrganizationIdAndProductId to the standard 'findById'
     * and removed the redundant branchId parameter.
     */
    @Override
    public Optional<StorefrontProduct> findById(String organizationId, String productId) {
        try {
            var doc = getCollection(organizationId).document(productId).get().get();
            return doc.exists() ? Optional.ofNullable(doc.toObject(StorefrontProduct.class)) : Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding storefront product by ID: " + productId, e);
        }
    }

    @Override
    public List<StorefrontProduct> findAllByOrganizationId(String organizationId) {
        try {
            var documents = getCollection(organizationId).get().get().getDocuments();
            return documents.stream()
                    .map(doc -> doc.toObject(StorefrontProduct.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error fetching all storefront products for organization: " + organizationId, e);
        }
    }

    /**
     * CORRECTED: Renamed from findAllVisibleByCategory.
     * This query is now simpler as it doesn't need to be a collection group query.
     */
    @Override
    public List<StorefrontProduct> findAllVisibleByCategoryId(String organizationId, String categoryId) {
        try {
            Query query = getCollection(organizationId)
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
