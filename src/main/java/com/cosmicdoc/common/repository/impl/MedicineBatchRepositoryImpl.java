package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.MedicineBatch;
import com.cosmicdoc.common.repository.MedicineBatchRepository;
import com.google.cloud.firestore.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class MedicineBatchRepositoryImpl implements MedicineBatchRepository {

    private Firestore firestore;

    public  MedicineBatchRepositoryImpl (Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Private helper to get a reference to the 'batches' sub-collection for a specific medicine.
     */
    private CollectionReference getCollection(String organizationId, String branchId, String medicineId) {
        return firestore.collection("organizations").document(organizationId)
                .collection("branches").document(branchId)
                .collection("medicines").document(medicineId)
                .collection("batches");
    }
    @Override
    public void saveInTransaction(Transaction transaction, String orgId, String branchId, String medicineId, MedicineBatch batch) {
        DocumentReference batchRef = getCollection(orgId, branchId, medicineId).document(batch.getBatchId());
        transaction.set(batchRef, batch);
    }

    /**
     * This is the core FEFO query implementation.
     */
    @Override
    public List<MedicineBatch> findAvailableBatches(Transaction transaction, String orgId, String branchId, String medicineId)
            throws ExecutionException, InterruptedException {

        // 1. Build the query to find all batches for this medicine...
        Query query = getCollection(orgId, branchId, medicineId)
                // ...where there is quantity available...
                .whereGreaterThan("quantityAvailable", 0)
                .orderBy("quantityAvailable")
                // ...and order them by the expiry date, soonest first.
                .orderBy("expiryDate", Query.Direction.ASCENDING);

        // 2. Execute the query using the transaction object.
        List<QueryDocumentSnapshot> documents = transaction.get(query).get().getDocuments();

        // 3. Map the results to a list of MedicineBatch objects.
        return documents.stream()
                .map(doc -> doc.toObject(MedicineBatch.class))
                .collect(Collectors.toList());
    }

    /**
     * This is the atomic stock update implementation for a specific batch.
     */
    @Override
    public void updateStockInTransaction(Transaction transaction, String orgId, String branchId, String medicineId, String batchId, int quantityChange) {
        // Get a direct reference to the specific batch document.
        DocumentReference batchRef = getCollection(orgId, branchId, medicineId).document(batchId);

        // Use the transaction object to stage an atomic increment/decrement operation.
        transaction.update(batchRef, "quantityAvailable", FieldValue.increment(quantityChange));
    }

    /**
     * Implementation for finding all batches for a medicine.
     */
    @Override
    public List<MedicineBatch> findAllBatchesForMedicine(String orgId, String branchId, String medicineId) {
        try {
            // 1. Get a reference to the specific 'batches' sub-collection.
            CollectionReference batchesCollection = getCollection(orgId, branchId, medicineId);

            // 2. Execute a '.get()' query to retrieve all documents in that collection.
            List<QueryDocumentSnapshot> documents = batchesCollection.get().get().getDocuments();

            // 3. Use a Java Stream to map each Firestore document to a MedicineBatch object.
            return documents.stream()
                    .map(doc -> doc.toObject(MedicineBatch.class))
                    .collect(Collectors.toList());

        } catch (InterruptedException | ExecutionException e) {
            // In a production environment, you should log this error properly.
            // Wrapping in a RuntimeException is a standard approach to propagate the failure.
            throw new RuntimeException("Error finding all batches for medicine: " + medicineId, e);
        }
    }

    /**
     * Implementation for finding a specific batch by its batchNo within a transaction.
     */
    @Override
    public Optional<MedicineBatch> findByBatchNo(Transaction transaction, String orgId, String branchId, String medicineId, String batchNo)
            throws ExecutionException, InterruptedException {

        // 1. Build the query to find the document where 'batchNo' matches.
        //    We limit it to 1 because batch numbers should be unique per medicine.
        Query query = getCollection(orgId, branchId, medicineId)
                .whereEqualTo("batchNo", batchNo)
                .limit(1);

        // 2. Execute the query using the provided transaction object.
        List<QueryDocumentSnapshot> documents = transaction.get(query).get().getDocuments();

        // 3. Check if any document was found.
        if (documents.isEmpty()) {
            return Optional.empty(); // If not found, return an empty Optional.
        }

        // 4. If found, convert the first document to a MedicineBatch object and return it.
        return Optional.ofNullable(documents.get(0).toObject(MedicineBatch.class));
    }

    @Override
    public void deleteByIdInTransaction(Transaction transaction, String orgId, String branchId, String medicineId, String batchId) {
        // Use the correct helper to get the path to the 'batches' sub-collection
        DocumentReference batchRef = getCollection(orgId, branchId, medicineId).document(batchId);

        // Stage the delete operation on the transaction
        transaction.delete(batchRef);
    }

    /**
     * Implementation for finding a specific MedicineBatch by its document ID within a transaction.
     */
    @Override
    public Optional<MedicineBatch> findById(Transaction transaction, String orgId, String branchId, String medicineId, String batchId)
            throws ExecutionException, InterruptedException {

        // 1. Get a direct reference to the batch document using the full path context.
        DocumentReference docRef = getCollection(orgId, branchId, medicineId).document(batchId);

        // 2. Use the transaction object to perform the read. This ensures the read is
        //    part of the atomic operation and that Firestore places a lock on the document.
        DocumentSnapshot snapshot = transaction.get(docRef).get();

        // 3. Check if the document exists and map it to the MedicineBatch object.
        if (snapshot.exists()) {
            return Optional.ofNullable(snapshot.toObject(MedicineBatch.class));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<DocumentSnapshot> getAll(Transaction transaction, String orgId, String branchId, String medicineId, List<String> batchIds) throws ExecutionException, InterruptedException {
        List<DocumentReference> docRefs = batchIds.stream()
                .map(id -> getCollection(orgId, branchId, medicineId).document(id))
                .collect(Collectors.toList());
        return transaction.getAll(docRefs.toArray(new DocumentReference[0])).get();
    }
}
