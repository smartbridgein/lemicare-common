package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.StorefrontOrder;
import com.cosmicdoc.common.repository.StorefrontOrderRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository

public class StorefrontOrderRepositoryImpl implements StorefrontOrderRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "storefront_orders";

    public StorefrontOrderRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    private CollectionReference getCollection(String organizationId, String branchId) {
        return firestore.collection("organizations").document(organizationId)
                .collection("branches").document(branchId)
                .collection(COLLECTION_NAME);
    }

    @Override
    public void saveInTransaction(Transaction transaction, StorefrontOrder order) {
        var docRef = getCollection(order.getOrganizationId(), order.getBranchId())
                .document(order.getOrderId());
        transaction.set(docRef, order);
    }
}
