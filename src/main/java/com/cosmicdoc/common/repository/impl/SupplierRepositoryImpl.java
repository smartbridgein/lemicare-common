package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Supplier;
import com.cosmicdoc.common.repository.SupplierRepository;
import com.google.cloud.firestore.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Firestore implementation of the SupplierRepository.
 * Manages Supplier documents which are stored as a sub-collection
 * under a specific organization.
 */
@Repository

public class SupplierRepositoryImpl implements SupplierRepository {

    private final Firestore firestore;

    public SupplierRepositoryImpl (Firestore firestore) {
        this.firestore = firestore;
    }
    /**
     * A private helper method to get a reference to the 'suppliers' sub-collection
     * for a specific organization. This ensures all methods use the correct,
     * consistent path.
     */
    private CollectionReference getCollection(String organizationId) {
        return firestore.collection("organizations").document(organizationId).collection("suppliers");
    }

    /**
     * Finds all suppliers for a specific organization.
     * This is useful for populating dropdowns or listing all vendors.
     */
    @Override
    public List<Supplier> findAllByOrganizationId(String organizationId) {
        try {
            // Get all documents from the specific organization's 'suppliers' collection
            var documents = getCollection(organizationId).get().get().getDocuments();

            // Use a modern Java Stream to map the Firestore documents to your Supplier model class
            return documents.stream()
                    .map(doc -> doc.toObject(Supplier.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            // In a production application, this error should be logged properly
            throw new RuntimeException("Error finding all suppliers for organization: " + organizationId, e);
        }
    }


    @Override
    public Supplier save(String organizationId, Supplier supplier) {
        try {
            getCollection(organizationId).document(supplier.getSupplierId()).set(supplier).get();
            return supplier;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error saving supplier: " + supplier.getName(), e);
        }
    }


    /**
     * Performs a "soft delete" on a supplier by changing its status to INACTIVE.
     * This preserves the supplier record for historical and auditing purposes
     * while effectively removing it from active use.
     *
     * @param organizationId The ID of the organization the supplier belongs to.
     * @param supplierId The ID of the supplier to be "deleted".
     */
    @Override
    public void deleteById(String organizationId, String supplierId) {
        try {
            // 1. Get a reference to the specific supplier document.
            var docRef = getCollection(organizationId).document(supplierId);

            // 2. Use the .update() method to change only specific fields.
            //    This is more efficient than fetching the whole object, changing a field,
            //    and saving it back with .set().
            Map<String, Object> updates = Map.of("status", "INACTIVE");

            // 3. Execute the update. The .get() call waits for the operation to complete.
            //    If the document with supplierId does not exist, this will throw an exception,
            //    which is the correct behavior.
            docRef.update(updates).get();

        } catch (InterruptedException | ExecutionException e) {
            // In a real application, log this exception for debugging.
            throw new RuntimeException("Error deleting (soft delete) supplier with ID: " + supplierId, e);
        }
    }
    /**
     * Finds a specific supplier by its ID within the given organization's sub-collection.
     * This is a direct and efficient document lookup.
     */
    @Override
    public Optional<Supplier> findById(String organizationId, String supplierId) {
        try {
            // 1. Get a direct reference to the document using the full path context.
            var documentSnapshot = getCollection(organizationId).document(supplierId).get().get();

            // 2. Check if the document actually exists in the database.
            if (documentSnapshot.exists()) {
                // 3. If it exists, convert it to a Supplier object.
                //    Using Optional.ofNullable is a safe practice in case the document
                //    is empty or malformed, which would cause toObject() to return null.
                return Optional.ofNullable(documentSnapshot.toObject(Supplier.class));
            } else {
                // 4. If the document does not exist, return an empty Optional
                //    to signal that the resource was not found.
                return Optional.empty();
            }
        } catch (InterruptedException | ExecutionException e) {
            // In a production application, log this exception for debugging.
            throw new RuntimeException("Error finding supplier with ID: " + supplierId + " in organization " + organizationId, e);
        }
    }

    @Override
    public boolean existsById(Transaction transaction, String orgId, String supplierId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getCollection(orgId).document(supplierId);
        return transaction.get(docRef).get().exists();
    }

    @Override
    public void updateBalanceInTransaction(Transaction transaction, String orgId, String supplierId, double amountChange) {
        // 1. Get a direct reference to the specific supplier document.
        DocumentReference supplierRef = getCollection(orgId).document(supplierId);

        // 2. Use the transaction object to stage an atomic increment operation.
        //    FieldValue.increment() tells the Firestore server to "add this value
        //    to the existing value", which is safe from concurrent updates.
        //    If the 'outstandingBalance' field doesn't exist, it will be created
        //    with this value.
        transaction.update(supplierRef, "outstandingBalance", FieldValue.increment(amountChange));
    }
}
