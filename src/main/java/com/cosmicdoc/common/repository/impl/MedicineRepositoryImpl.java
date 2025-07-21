package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Medicine;
import com.cosmicdoc.common.repository.MedicineRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Firestore implementation of the MedicineRepository.
 * Manages Medicine documents which are stored as a sub-collection
 * under a specific organization's branch.
 */
@Repository

public class MedicineRepositoryImpl implements MedicineRepository {

    private Firestore firestore;

   public  MedicineRepositoryImpl (Firestore firestore) {
       this.firestore = firestore;
   }

    /**
     * A private helper method to get a reference to the 'medicines' sub-collection
     * for a specific organization and branch. This ensures all methods use the correct,
     * consistent path and is the core of the branch-centric design.
     */
    private CollectionReference getCollection(String organizationId, String branchId) {
        return firestore.collection("organizations").document(organizationId)
                .collection("branches").document(branchId)
                .collection("medicines");
    }

    /**
     * Saves a medicine document (creates if new, overwrites if exists).
     * This is used for creating or updating the master details of a medicine.
     */
    @Override
    public Medicine save(String organizationId, String branchId, Medicine medicine) {
        try {
            getCollection(organizationId, branchId).document(medicine.getMedicineId()).set(medicine).get();
            return medicine;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error saving medicine with ID: " + medicine.getMedicineId(), e);
        }
    }

    /**
     * Finds a specific medicine by its ID within a given branch.
     * This is a direct and fast document lookup.
     */
    @Override
    public Optional<Medicine> findById(String organizationId, String branchId, String medicineId) {
        try {
            var document = getCollection(organizationId, branchId).document(medicineId).get().get();
            if (document.exists()) {
                // Use ofNullable for safety against malformed data in the database
                return Optional.ofNullable(document.toObject(Medicine.class));
            }
            return Optional.empty(); // Return empty if the document does not exist
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding medicine with ID: " + medicineId, e);
        }
    }

    /**
     * Finds all medicines for a specific branch.
     * Useful for listing inventory.
     */
    @Override
    public List<Medicine> findAllByBranchId(String organizationId, String branchId) {
        try {
            var documents = getCollection(organizationId, branchId).get().get().getDocuments();
            // Use a Java Stream to map the Firestore documents to your model class
            return documents.stream()
                    .map(doc -> doc.toObject(Medicine.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding all medicines for branch: " + branchId, e);
        }
    }

    /**
     * Adds an atomic stock update operation to a WriteBatch.
     * This is the core method for ensuring inventory consistency during
     * purchases, sales, and returns.
     */
    @Override
    public void updateStockInTransaction(Transaction batch, String organizationId, String branchId, String medicineId, int quantityChange) {
        // Get a reference to the specific medicine document
        var docRef = getCollection(organizationId, branchId).document(medicineId);

        // Use FieldValue.increment() for an atomic, server-side update.
        // Pass a positive value (e.g., 10) to increase stock (for purchases/sales returns).
        // Pass a negative value (e.g., -2) to decrease stock (for sales/purchase returns).
        batch.update(docRef, "quantityInStock", FieldValue.increment(quantityChange));
    }

    /**
     * Performs a "soft delete" on a medicine by changing its status to INACTIVE.
     * <p>
     * This preserves the medicine record for historical data integrity (e.g., for old invoices)
     * while effectively removing it from active use in lookups or new transactions.
     * The operation is targeted and efficient, only updating the status field.
     *
     * @param organizationId The ID of the organization the medicine belongs to.
     * @param branchId The ID of the branch the medicine belongs to.
     * @param medicineId The ID of the medicine to be "deleted".
     */
    @Override
    public void deleteById(String organizationId, String branchId, String medicineId) {
        try {
            // 1. Get a direct reference to the specific medicine document
            //    using the full path context.
            var docRef = getCollection(organizationId, branchId).document(medicineId);

            // 2. Create a Map containing ONLY the field(s) to be updated.
            //    This is the standard way to perform a partial update in Firestore.
            Map<String, Object> updates = Map.of("status", "INACTIVE");

            // 3. Execute the atomic update operation on the Firestore server.
            //    The .get() call waits for the operation to complete and will throw
            //    an exception if the document does not exist, which is correct behavior.
            docRef.update(updates).get();

        } catch (InterruptedException | ExecutionException e) {
            // In a production application, this exception should be logged for debugging.
            throw new RuntimeException("Error performing soft delete on medicine with ID: " + medicineId, e);
        }
    }

    public void updateStockInTransaction(WriteBatch batch, String organizationId, String branchId, String medicineId, int quantityChange) {
        // Get a reference to the specific medicine document
        var docRef = getCollection(organizationId, branchId).document(medicineId);

        // Use FieldValue.increment() for an atomic, server-side update.
        // Pass a positive value (e.g., 10) to increase stock (for purchases/sales returns).
        // Pass a negative value (e.g., -2) to decrease stock (for sales/purchase returns).
        batch.update(docRef, "quantityInStock", FieldValue.increment(quantityChange));
    }

    @Override
    public List<DocumentSnapshot> getAll(Transaction transaction, String orgId, String branchId, List<String> medicineIds) throws ExecutionException, InterruptedException {
        List<DocumentReference> docRefs = medicineIds.stream()
                .map(id -> getCollection(orgId, branchId).document(id))
                .collect(Collectors.toList());
        return transaction.getAll(docRefs.toArray(new DocumentReference[0])).get();
    }

    @Override
    public List<Medicine> findAllByIds(String orgId, String branchId, List<String> medicineIds) {
        if (medicineIds == null || medicineIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Build a list of DocumentReferences
        List<DocumentReference> refs = medicineIds.stream()
                .map(id -> getCollection(orgId, branchId).document(id))
                .collect(Collectors.toList());

        try {
            // Use the highly efficient getAll() method
            return firestore.getAll(refs.toArray(new DocumentReference[0])).get()
                    .stream()
                    .filter(DocumentSnapshot::exists)
                    .map(doc -> doc.toObject(Medicine.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error fetching medicines by IDs", e);
        }
    }

    @Override
    public Optional<Medicine> findById(Transaction transaction, String orgId, String branchId, String medicineId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getCollection(orgId, branchId).document(medicineId);
        DocumentSnapshot snapshot = transaction.get(docRef).get();
        return snapshot.exists() ? Optional.ofNullable(snapshot.toObject(Medicine.class)) : Optional.empty();
    }

    @Override
    public Optional<Medicine> findByNameIgnoreCaseExcludingId(String orgId, String branchId, String name, String excludeMedicineId) {
        try {
            // Firestore queries are case-sensitive. The standard approach is to store
            // a normalized (e.g., lowercase) version of the field for case-insensitive searches.
            // Assuming you add a 'normalizedName' field to your Medicine model.
            Query query = getCollection(orgId, branchId)
                    .whereEqualTo("normalizedName", name.toLowerCase())
                    .limit(1); // We only need to know if at least one exists.

            List<QueryDocumentSnapshot> documents = query.get().get().getDocuments();

            if (documents.isEmpty()) {
                return Optional.empty(); // No medicine with this name found.
            }

            // If a document is found, check if it's the one we're trying to update.
            Medicine foundMedicine = documents.get(0).toObject(Medicine.class);
            if (foundMedicine.getMedicineId().equals(excludeMedicineId)) {
                return Optional.empty(); // The conflict is with the same document, so it's okay.
            }

            // A different document with the same name exists, so it's a true conflict.
            return Optional.of(foundMedicine);

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error checking for medicine by name", e);
        }
    }

    @Override
    public boolean existsByTaxProfileId(String orgId, String taxProfileId) {
        // This query needs to scan across all branches, so it's a Collection Group query.
        Query query = firestore.collectionGroup("medicines")
                .whereEqualTo("organizationId", orgId) // Scope to the organization
                .whereEqualTo("taxProfileId", taxProfileId)
                .whereEqualTo("status", "ACTIVE") // Only check active medicines
                .limit(1);
        try {
            return !query.get().get().isEmpty();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error checking for medicine by tax profile", e);
        }
    }

    @Override
    public void deleteByIdHard(String organizationId, String branchId, String medicineId) {
        try {
            // 1. Get a reference to the 'batches' sub-collection for the given medicine.
            CollectionReference batchesRef = getCollection(organizationId, branchId)
                    .document(medicineId)
                    .collection("batches");

            // 2. Get a reference to the parent Medicine document itself.
            DocumentReference medicineRef = getCollection(organizationId, branchId).document(medicineId);

            // 3. Start a WriteBatch to group all delete operations.
            WriteBatch batch = firestore.batch();

            // 4. Find all documents in the sub-collection and add them to the batch for deletion.
            //    listDocuments() is efficient as it doesn't download the document data.
            Iterable<DocumentReference> batchDocuments = batchesRef.listDocuments();
            for (DocumentReference ref : batchDocuments) {
                batch.delete(ref);
            }

            // 5. Add the parent Medicine document to the batch for deletion.
            batch.delete(medicineRef);

            // 6. Atomically commit all the delete operations.
            //    Either everything is deleted, or nothing is.
            batch.commit().get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error performing hard delete on medicine with ID: " + medicineId, e);
        }
    }
}

