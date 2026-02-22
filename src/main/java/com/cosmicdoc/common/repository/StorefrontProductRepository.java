package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.StorefrontProduct;
import com.cosmicdoc.common.util.CursorPage;
import com.google.firebase.database.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing StorefrontProduct entities.
 * These are stored as a sub-collection under an Organization.
 */
public interface StorefrontProductRepository {

    /**
     * Saves a new or updated StorefrontProduct. The implementation derives the
     * storage path from the 'organizationId' within the product object.
     */
    StorefrontProduct save(StorefrontProduct product);

    /**
     * Finds a single storefront product by its ID within a specific organization.
     */
    Optional<StorefrontProduct> findById(String organizationId, String productId);

    /**
     * Finds all storefront products for a specific organization.
     */
    List<StorefrontProduct> findAllByOrganizationId(String organizationId);

    /**
     * Finds all storefront products in a specific category for a given organization.
     */
    List<StorefrontProduct> findAllVisibleByCategoryId(String organizationId, String categoryId);

    CursorPage<StorefrontProduct> findAllVisible(String organizationId, String categoryId, int pageSize, @Nullable String startAfter);

    void deleteByProductId(String organizationId,String productId);

    List<StorefrontProduct> findAllByOrganizationIdAndProductIdIn(
            String organizationId,
            List<String> productIds);
}