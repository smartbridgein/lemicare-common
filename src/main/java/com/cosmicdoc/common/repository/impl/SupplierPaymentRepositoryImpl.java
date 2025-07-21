package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.SupplierPayment;
import com.cosmicdoc.common.repository.SupplierPaymentRepository;
import com.google.cloud.firestore.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SupplierPaymentRepositoryImpl implements SupplierPaymentRepository {

    private final Firestore firestore;
    private static final String SUPPLIERS_COLLECTION = "suppliers";
    private static final String PAYMENTS_SUBCOLLECTION = "payments";

    /**
     * Private helper to get a reference to the 'payments' sub-collection for a specific supplier.
     */
    private CollectionReference getCollection(String organizationId, String supplierId) {
        return firestore.collection("organizations").document(organizationId)
                .collection(SUPPLIERS_COLLECTION).document(supplierId)
                .collection(PAYMENTS_SUBCOLLECTION);
    }

    /**
     * Implementation for saving a payment within a transaction.
     */
    @Override
    public void saveInTransaction(Transaction transaction, String organizationId, String supplierId, SupplierPayment payment) {
        // 1. Validate that the payment object contains a unique ID.
        if (payment.getPaymentId() == null) {
            throw new IllegalArgumentException("PaymentId must not be null to save a payment record.");
        }

        // 2. Get a reference to the new document within the correct sub-collection path.
        DocumentReference paymentRef = getCollection(organizationId, supplierId)
                .document(payment.getPaymentId());

        // 3. Stage the 'set' operation on the transaction.
        transaction.set(paymentRef, payment);
    }

    @Override
    public List<SupplierPayment> findAllBySupplierId(String organizationId, String supplierId) {
        try {
            // 1. Get a reference to the specific supplier's 'payments' sub-collection.
            CollectionReference paymentsCollection = getCollection(organizationId, supplierId);

            // 2. Build the query to fetch all documents, ordered by date descending.
            Query query = paymentsCollection.orderBy("paymentDate", Query.Direction.DESCENDING);

            // 3. Execute the query and map the results.
            return query.get().get().getDocuments().stream()
                    .map(doc -> doc.toObject(SupplierPayment.class))
                    .collect(Collectors.toList());

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding all payments for supplier: " + supplierId, e);
        }
    }

    @Override
    public List<SupplierPayment> findAllByPurchaseInvoiceId(String organizationId, String supplierId, String purchaseInvoiceId) {
        try {
            // 1. Get a reference to the supplier's 'payments' sub-collection.
            CollectionReference paymentsCollection = getCollection(organizationId, supplierId);

            // 2. Build the query to find documents where the 'purchaseInvoiceId' field matches.
            Query query = paymentsCollection.whereEqualTo("purchaseInvoiceId", purchaseInvoiceId)
                    .orderBy("paymentDate", Query.Direction.ASCENDING);

            // 3. Execute and map the results.
            return query.get().get().getDocuments().stream()
                    .map(doc -> doc.toObject(SupplierPayment.class))
                    .collect(Collectors.toList());

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding payments for invoice: " + purchaseInvoiceId, e);
        }
    }

    @Override
    public void deleteAllByPurchaseIdInTransaction(Transaction transaction, String orgId, String supplierId, String purchaseId) throws ExecutionException, InterruptedException {
        Query query = getCollection(orgId, supplierId).whereEqualTo("purchaseInvoiceId", purchaseId);
        List<QueryDocumentSnapshot> paymentsToDelete = transaction.get(query).get().getDocuments();
        for (DocumentSnapshot doc : paymentsToDelete) {
            transaction.delete(doc.getReference());
        }
    }
}
