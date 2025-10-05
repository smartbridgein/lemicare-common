package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.CartItem;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface CartItemRepository {
    List<CartItem> findByCartId(String cartId) throws ExecutionException, InterruptedException;
    Optional<CartItem> findByCartIdAndProductId(String cartId, String productId) throws ExecutionException, InterruptedException;
    Optional<CartItem> findById(String cartItemId) throws ExecutionException, InterruptedException;
    CartItem save(CartItem cartItem) throws ExecutionException, InterruptedException;
    public void deleteAllByCartId(String cartId) throws ExecutionException, InterruptedException;

}
