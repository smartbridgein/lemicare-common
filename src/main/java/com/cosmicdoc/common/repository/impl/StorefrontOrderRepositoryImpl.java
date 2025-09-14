package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.StorefrontOrder;
import com.cosmicdoc.common.repository.StorefrontOrderRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;

@Repository

public class StorefrontOrderRepositoryImpl implements StorefrontOrderRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "storefront_orders";

    public StorefrontOrderRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }


    @Override
    public void saveInTransaction(Transaction transaction, StorefrontOrder order) {
        var docRef = getCollection(order.getOrganizationId())
                .document(order.getOrderId());
        transaction.set(docRef, order);
    }

    public StorefrontOrder save(StorefrontOrder order) {
        if (order.getOrganizationId() == null || order.getOrderId() == null) {
            throw new IllegalArgumentException("StorefrontOrder must have organizationId and orderId for saving.");
        }
        try {
            // Using set() with the provided orderId as the document ID
            this.getCollection(order.getOrganizationId())
                    .document(order.getOrderId())
                    .set(order)
                    .get();
            return order;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error saving storefront order with ID: " + order.getOrderId(), e);
        }
    }

    private CollectionReference getCollection(String organizationId) {
        return firestore.collection("organizations")
                .document(organizationId)
                .collection(COLLECTION_NAME);
    }
}
