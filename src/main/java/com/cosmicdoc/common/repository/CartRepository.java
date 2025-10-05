package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.Cart;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface CartRepository {
    Optional<Cart> findByOrgIdAndUserId(String orgId, String userId) throws ExecutionException, InterruptedException;
    Optional<Cart> findByOrgIdAndGuestId(String orgId, String guestId) throws ExecutionException, InterruptedException;
    Optional<Cart> findById(String cartId) throws ExecutionException, InterruptedException;
    Cart save(Cart cart) throws ExecutionException, InterruptedException;
    void delete(String cartId) throws ExecutionException, InterruptedException;


}
