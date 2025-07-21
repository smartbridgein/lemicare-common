package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Purchase;
import com.cosmicdoc.common.model.Sale;
import com.cosmicdoc.common.repository.SaleRepository;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Firestore implementation of the SaleRepository.
 * Manages Sale documents which are stored as a sub-collection
 * under a specific organization's branch.
 */
@Repository

public class SaleRepositoryImpl implements SaleRepository {

    private final Firestore firestore;

    public SaleRepositoryImpl (Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * A private helper method to get a reference to the 'sales' sub-collection
     * for a specific organization and branch. This centralizes the path logic
     * and ensures consistency across all methods.
     */
    private CollectionReference getCollection(String organizationId, String branchId) {
        return firestore.collection("organizations").document(organizationId)
                .collection("branches").document(branchId)
                .collection("sales");
    }

    /**
     * Finds a specific sales invoice by its ID within a given branch.
     * This is a direct and efficient document lookup.
     */
    @Override
    public Optional<Sale> findById(String organizationId, String branchId, String saleId) {
        try {
            var document = getCollection(organizationId, branchId).document(saleId).get().get();
            if (document.exists()) {
                // Use ofNullable for safety against malformed data in the database
                return Optional.ofNullable(document.toObject(Sale.class));
            }
            // Return an empty Optional if the document is not found
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            // In a production app, this should be logged to a proper logging service
            throw new RuntimeException("Error finding sale with ID: " + saleId, e);
        }
    }

    /**
     * Finds all sales invoices for a specific branch.
     * This is useful for listing sales history and reporting.
     */
    @Override
    public List<Sale> findAllByBranchId(String organizationId, String branchId) {
        try {
            // Get all documents from the specific branch's 'sales' collection
            var documents = getCollection(organizationId, branchId).get().get().getDocuments();

            // Use a modern Java Stream to map the Firestore documents to your Sale model class
            return documents.stream()
                    .map(doc -> doc.toObject(Sale.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            // Log this error in a real application
            throw new RuntimeException("Error finding all sales for branch: " + branchId, e);
        }
    }

    /**
     * Adds a "set" operation for a new Sale document to a given WriteBatch.
     * This method's responsibility is only to add the operation to the transaction.
     * The calling service is responsible for committing the batch.
     */
    @Override
    public void saveInTransaction(WriteBatch batch, String organizationId, String branchId, Sale sale) {
        // Get a reference to the new document within the correct sub-collection path
        var docRef = getCollection(organizationId, branchId).document(sale.getSaleId());

        // Stage the 'set' operation in the batch.
        batch.set(docRef, sale);
    }

    @Override
    public void saveInTransaction(Transaction transaction, Sale sale) {
        // 1. Validate that the purchase object contains the necessary context.
        if (sale.getOrganizationId() == null || sale.getBranchId() == null  || sale.getSaleId() == null) {
            throw new IllegalArgumentException("OrganizationId ,Branch Id and PurchaseId must not be null to save a sale.");
        }
        // 2. Extract the context FROM THE OBJECT to build the correct path.
        //    This assumes a sub-collection model: /organizations/{orgId}/purchases/{purchaseId}
        var docRef = getCollection(sale.getOrganizationId(),sale.getBranchId()).document(sale.getSaleId());
        // 3. Queue the 'set' operation on the transaction.
        transaction.set(docRef, sale);
    }

    /**
     * Implementation for finding a specific Sale by its ID within a transaction.
     */
    @Override
    public Optional<Sale> findById(Transaction transaction, String orgId, String branchId, String saleId)
            throws ExecutionException, InterruptedException {

        // 1. Get a direct reference to the document using the full path context.
        DocumentReference docRef = getCollection(orgId, branchId).document(saleId);

        // 2. Use the transaction object to perform the read.
        DocumentSnapshot snapshot = transaction.get(docRef).get();

        // 3. Check if the document exists and map it to the Sale object.
        if (snapshot.exists()) {
            return Optional.ofNullable(snapshot.toObject(Sale.class));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Sale> findAllByBranchIdAndDate(String orgId, String branchId, LocalDate date) {
        try {
            // 1. Calculate the start of the target day in UTC.
            // e.g., for date "2025-06-30", this becomes Timestamp("2025-06-30T00:00:00Z")
            Timestamp startOfDay = Timestamp.of(Date.from(date.atStartOfDay().toInstant(ZoneOffset.UTC)));

            // 2. Calculate the start of the NEXT day in UTC.
            // e.g., for date "2025-06-30", this becomes Timestamp("2025-07-01T00:00:00Z")
            Timestamp startOfNextDay = Timestamp.of(Date.from(date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)));

            // 3. Build the Firestore range query.
            // Find all documents where 'saleDate' is >= startOfDay AND < startOfNextDay.
            Query query = getCollection(orgId, branchId)
                    .whereGreaterThanOrEqualTo("saleDate", startOfDay)
                    .whereLessThan("saleDate", startOfNextDay);

            // 4. Execute the query and map the results.
            return query.get().get().getDocuments().stream()
                    .map(doc -> doc.toObject(Sale.class))
                    .collect(Collectors.toList());

        } catch (InterruptedException | ExecutionException e) {
            // In a production app, log this error properly.
            throw new RuntimeException("Error finding sales for branch " + branchId + " on date " + date, e);
        }
    }

    @Override
    public void deleteByIdInTransaction(Transaction transaction, String orgId, String branchId, String saleId) {
        // 1. Get a direct reference to the document using the full path context.
        DocumentReference docRef = getCollection(orgId, branchId).document(saleId);

        // 2. Use the transaction object to stage the delete operation.
        //    This does not execute immediately; it becomes part of the atomic transaction.
        transaction.delete(docRef);
    }
}
