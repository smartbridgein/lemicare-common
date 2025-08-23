package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.StorefrontProduct;

import java.util.List;
import java.util.Optional;

public interface StorefrontProductRepository {

    StorefrontProduct save(StorefrontProduct product,String organizationId, String branchId);

    Optional<StorefrontProduct> findById(String organizationId, String branchId, String productId);

    // For the public storefront to list all visible products in a branch
    List<StorefrontProduct> findAllVisibleByBranch(String organizationId, String branchId);

    // For the public storefront to list all visible products in a category
    List<StorefrontProduct> findAllVisibleByCategory(String organizationId, String categoryId);
}
