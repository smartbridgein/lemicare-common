package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.StorefrontCategory;
import com.cosmicdoc.common.model.StorefrontProduct;
import com.cosmicdoc.common.repository.StorefrontCategoryRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository

public class StorefrontCategoryRepositoryImpl implements StorefrontCategoryRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "storefront_categories";

    public StorefrontCategoryRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    private CollectionReference getCollection(String organizationId) {
        // Assuming categories are at the organization level
        return firestore.collection("organizations").document(organizationId).collection(COLLECTION_NAME);
    }

    @Override
    public StorefrontCategory save(StorefrontCategory category) {
        try {
            getCollection(category.getOrganizationId()).document(category.getCategoryId()).set(category).get();
            return category;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error saving storefront category", e);
        }
    }

    @Override
    public Optional<StorefrontCategory> findById(String organizationId, String categoryId) {
        try {
            var doc = getCollection(organizationId).document(categoryId).get().get();
            return doc.exists() ? Optional.ofNullable(doc.toObject(StorefrontCategory.class)) : Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding storefront category by ID", e);
        }
    }

    @Override
    public List<StorefrontCategory> findAllByOrganization(String organizationId) {
        try {
            return getCollection(organizationId).get().get().getDocuments().stream()
                    .map(doc -> doc.toObject(StorefrontCategory.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error fetching all categories for organization", e);
        }
    }

    @Override
    public void deleteById(String organizationId, String categoryId) {
        try {
            getCollection(organizationId).document(categoryId).delete().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error deleting storefront category", e);
        }
    }

    /**
     * Implementation for finding a storefront product by its composite key (orgId and productId).
     */
    @Override
    public Optional<StorefrontProduct> findByOrganizationIdAndProductId(String organizationId, String productId) {
        try {
            // This is a direct and highly efficient document lookup.
            var document = getCollection(organizationId).document(productId).get().get();
            if (document.exists()) {
                return Optional.ofNullable(document.toObject(StorefrontProduct.class));
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding storefront product by ID: " + productId, e);
        }
    }
}
