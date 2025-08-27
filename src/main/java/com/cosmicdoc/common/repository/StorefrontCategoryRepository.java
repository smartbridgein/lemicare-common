package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.StorefrontCategory;
import com.cosmicdoc.common.model.StorefrontProduct;

import java.util.List;
import java.util.Optional;

public interface StorefrontCategoryRepository {
    StorefrontCategory save(StorefrontCategory category);
    Optional<StorefrontCategory> findById(String organizationId, String categoryId);
    List<StorefrontCategory> findAllByOrganization(String organizationId);
    void deleteById(String organizationId, String categoryId);
    Optional<StorefrontProduct> findByOrganizationIdAndProduct(String organizationId, String productId);

}
