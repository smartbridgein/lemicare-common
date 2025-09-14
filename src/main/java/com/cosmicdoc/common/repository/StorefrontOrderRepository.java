package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.StorefrontOrder;
import com.google.cloud.firestore.Transaction;

public interface StorefrontOrderRepository {
    // Orders are almost always created within a larger transaction
    void saveInTransaction(Transaction transaction, StorefrontOrder order);
    StorefrontOrder save(StorefrontOrder order);
}
