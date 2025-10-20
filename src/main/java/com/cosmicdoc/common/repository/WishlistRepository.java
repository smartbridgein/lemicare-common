package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.Wishlist;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface WishlistRepository {
    Optional<Wishlist> findByOrganizationIdAndCustomerId(String organizationId, String customerId) throws ExecutionException, InterruptedException;
    Wishlist save(Wishlist wishlist) throws ExecutionException, InterruptedException;
    void delete(String organizationId, String customerId) throws ExecutionException, InterruptedException;
}
