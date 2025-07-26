package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Purchase;
import com.cosmicdoc.common.repository.PurchaseRepository;
import com.google.cloud.firestore.*;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Firestore implementation of the PurchaseRepository.
 * Manages Purchase documents which are stored as a sub-collection
 * under a specific organization's branch.
 */
@Repository

public class PurchaseRepositoryImpl implements PurchaseRepository {

    private final Firestore firestore;

    public PurchaseRepositoryImpl (Firestore firestore) {
        this.firestore = firestore ;
    }

    /**
     * A private helper method to get a reference to the 'purchases' sub-collection
     * for a specific organization and branch. This centralizes the path logic
     * and ensures consistency across all methods.
     */
    private CollectionReference getCollection(String organizationId, String branchId) {
        return firestore.collection("organizations").document(organizationId)
                .collection("branches").document(branchId)
                .collection("purchases");
    }

    /**
     * Finds a specific purchase invoice by its ID within a given branch.
     * This is a direct and efficient document lookup.
     */
    @Override
    public Optional<Purchase> findById(String organizationId, String branchId, String purchaseId) {
        try {
            var document = getCollection(organizationId, branchId).document(purchaseId).get().get();
            if (document.exists()) {
                // Use ofNullable for safety against malformed data in the database
                return Optional.ofNullable(document.toObject(Purchase.class));
            }
            // Return an empty Optional if the document is not found
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            // In a production app, this should be logged to a proper logging service
            throw new RuntimeException("Error finding purchase with ID: " + purchaseId, e);
        }
    }

    /**
     * Finds all purchase invoices for a specific branch.
     * This is useful for listing purchase history and reporting.
     */
    @Override
    public List<Purchase> findAllByBranchId(String organizationId, String branchId) {
        try {
            // Get all documents from the specific branch's 'purchases' collection
            var documents = getCollection(organizationId, branchId).get().get().getDocuments();

            // Use a modern Java Stream to map the Firestore documents to your Purchase model class
            return documents.stream()
                    .map(doc -> doc.toObject(Purchase.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            // Log this error in a real application
            throw new RuntimeException("Error finding all purchases for branch: " + branchId, e);
        }
    }

    /**
     * Adds a "set" operation for a new Purchase document to a given WriteBatch.
     * This method's responsibility is only to add the operation to the transaction.
     * The calling service is responsible for committing the batch.
     */
    @Override
    public void saveInTransaction(WriteBatch batch, String organizationId, String branchId, Purchase purchase) {
        // Get a reference to the new document within the correct sub-collection path
        var docRef = getCollection(organizationId, branchId).document(purchase.getPurchaseId());

        // Stage the 'set' operation in the batch.
        batch.set(docRef, purchase);
    }

    @Override
    public void saveInTransaction(Transaction transaction, Purchase purchase) {
        // 1. Validate that the purchase object contains the necessary context.
        if (purchase.getOrganizationId() == null || purchase.getBranchId() == null  || purchase.getPurchaseId() == null) {
            throw new IllegalArgumentException("OrganizationId ,Branch Id and PurchaseId must not be null to save a purchase.");
        }
        // 2. Extract the context FROM THE OBJECT to build the correct path.
        //    This assumes a sub-collection model: /organizations/{orgId}/purchases/{purchaseId}
        var docRef = getCollection(purchase.getOrganizationId(),purchase.getBranchId()).document(purchase.getPurchaseId());
        // 3. Queue the 'set' operation on the transaction.
        transaction.set(docRef, purchase);
    }

    /**
     * Implementation for finding a specific Purchase by its ID within a transaction.
     */
    @Override
    public Optional<Purchase> findById(Transaction transaction, String orgId, String branchId, String purchaseId)
            throws ExecutionException, InterruptedException {

        // 1. Get a direct reference to the document using the full path context.
        DocumentReference docRef = getCollection(orgId, branchId).document(purchaseId);

        // 2. Use the transaction object to perform the read. This ensures the read is
        //    part of the atomic operation.
        DocumentSnapshot snapshot = transaction.get(docRef).get();

        // 3. Check if the document exists and map it to the Purchase object.
        if (snapshot.exists()) {
            return Optional.ofNullable(snapshot.toObject(Purchase.class));
        } else {
            return Optional.empty();
        }
    }
    @Override
    public void deleteByIdInTransaction(Transaction transaction, String orgId, String branchId, String purchaseId) {
        DocumentReference docRef = getCollection(orgId, branchId).document(purchaseId);
        transaction.delete(docRef);
    }

    @Override
    public List<Purchase> findAllBySupplierId(String organizationId, String branchId, String supplierId) {
        try {
            // 1. Get a reference to the specific branch's 'purchases' sub-collection.
            CollectionReference purchasesCollection = getCollection(organizationId, branchId);

            // 2. Build the query to find all documents where the 'supplierId' field matches.
            //    We also order by date to provide a sensible, chronological list.
            Query query = purchasesCollection
                    .whereEqualTo("supplierId", supplierId)
                    .orderBy("invoiceDate", Query.Direction.DESCENDING); // Newest first

            // 3. Execute the query and map the results to Purchase objects.
            return query.get().get().getDocuments().stream()
                    .map(doc -> doc.toObject(Purchase.class))
                    .collect(Collectors.toList());

        } catch (InterruptedException | ExecutionException e) {
            // In a production app, log this error properly.
            throw new RuntimeException("Error finding all purchases for supplier: " + supplierId, e);
        }
    }
}
