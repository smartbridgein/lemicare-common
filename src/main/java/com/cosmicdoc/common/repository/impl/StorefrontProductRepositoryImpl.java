package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.StorefrontProduct;
import com.cosmicdoc.common.repository.StorefrontProductRepository;
import com.cosmicdoc.common.util.FirestorePage;
import com.google.api.gax.paging.Page;
import com.google.cloud.firestore.*;
import com.google.firebase.database.annotations.Nullable;
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

    public StorefrontProductRepositoryImpl (Firestore firestore) {
        this.firestore = firestore;
    }
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

    @Override
    public Page<StorefrontProduct> findAllVisible(String organizationId, String categoryId, int pageSize, @Nullable String startAfter) {
        try {
            // --- Base query: fetch only visible products ---
            Query baseQuery = getCollection(organizationId)
                    .whereEqualTo("isVisible", true)
                    .orderBy("productId");

            // --- Apply category filter only if categoryId is provided ---
            if (categoryId != null && !categoryId.isEmpty()) {
                baseQuery = baseQuery.whereEqualTo("categoryId", categoryId);
            }

            // --- Get total count for pagination ---
            long totalElements = 0;
            try {
                totalElements = baseQuery.count().get().get().getCount();
            } catch (Exception e) {
                System.err.println("Error getting total count for products: " + e.getMessage());
            }

            int totalPages = (int) Math.ceil((double) totalElements / pageSize);

            // --- Apply pagination cursor if provided ---
            Query paginatedQuery = baseQuery;
            if (startAfter != null && !startAfter.isEmpty()) {
                DocumentSnapshot startAfterDoc = getCollection(organizationId)
                        .document(startAfter)
                        .get()
                        .get();

                if (startAfterDoc.exists()) {
                    paginatedQuery = baseQuery.startAfter(startAfterDoc);
                } else {
                    System.err.println("Warning: startAfter document with ID " + startAfter + " not found. Starting from beginning.");
                }
            }

            // --- Fetch documents (limit + 1 to detect if next page exists) ---
            List<QueryDocumentSnapshot> documents = paginatedQuery.limit(pageSize + 1).get().get().getDocuments();

            List<StorefrontProduct> products = documents.stream()
                    .limit(pageSize)
                    .map(doc -> doc.toObject(StorefrontProduct.class))
                    .collect(Collectors.toList());

            // --- Handle pagination token & last-page flag ---
            String nextPageToken = null;
            boolean isLast = true;
            if (documents.size() > pageSize) {
                nextPageToken = products.get(products.size() - 1).getProductId();
                isLast = false;
            }

            // --- Return FirestorePage response ---
            return new FirestorePage<>(products, nextPageToken, pageSize, totalElements, totalPages, isLast);

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error fetching paginated visible products", e);
        }
    }
}
