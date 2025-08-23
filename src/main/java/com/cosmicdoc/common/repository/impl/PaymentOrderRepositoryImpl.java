package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.PaymentOrder;
import com.cosmicdoc.common.repository.PaymentOrderRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository

public class PaymentOrderRepositoryImpl implements PaymentOrderRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "payment_orders";

    public PaymentOrderRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    // --- THIS IS THE CRITICAL CHANGE ---
    private CollectionReference getCollection(String organizationId, String branchId) {
        return firestore.collection("organizations").document(organizationId)
                .collection("branches").document(branchId)
                .collection(COLLECTION_NAME);
    }

    @Override
    public PaymentOrder save(PaymentOrder paymentOrder) {
        // ... validation checks ...
        try {
            // The getCollection method now requires the branchId from the object.
            getCollection(paymentOrder.getOrganizationId(), paymentOrder.getBranchId())
                    .document(paymentOrder.getOrderId())
                    .set(paymentOrder).get();
            return paymentOrder;
        } catch (Exception e) {
            throw new RuntimeException("Error saving payment order", e);
        }
    }

    @Override
    public Optional<PaymentOrder> findByRazorpayOrderId(String organizationId, String branchId, String razorpayOrderId) {
        try {
            Query query = getCollection(organizationId, branchId) // Use the branch-specific collection
                    .whereEqualTo("razorpayOrderId", razorpayOrderId)
                    .limit(1);

            var documents = query.get().get().getDocuments();
            if (documents.isEmpty()) {
                return Optional.empty();
            }
            return Optional.ofNullable(documents.get(0).toObject(PaymentOrder.class));
        } catch (Exception e) {
            throw new RuntimeException("Error finding payment order", e);
        }
    }

    /**
     * Implementation for finding a payment order by its primary key (document ID).
     */
    @Override
    public Optional<PaymentOrder> findById(String organizationId, String branchId, String orderId) {
        try {
            // 1. Get a direct reference to the document using the full path and the document ID.
            DocumentSnapshot document = getCollection(organizationId, branchId).document(orderId).get().get();

            // 2. Check if the document exists.
            if (document.exists()) {
                // 3. If it exists, map it to the PaymentOrder object and return.
                return Optional.ofNullable(document.toObject(PaymentOrder.class));
            }
            // 4. If not found, return an empty Optional.
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            // In a production app, you should log this exception properly.
            throw new RuntimeException("Error finding payment order by ID: " + orderId, e);
        }
    }
}
