package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.SalesReturn;
import com.cosmicdoc.common.repository.SalesReturnRepository;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Transaction;
import com.google.cloud.firestore.WriteBatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
@Repository

public class SalesReturnRepositoryImpl  implements SalesReturnRepository {

   private final Firestore firestore;

   public SalesReturnRepositoryImpl (Firestore firestore) {
       this.firestore = firestore;
   }
    private CollectionReference getCollection(String organizationId, String branchId) {
        return firestore.collection("organizations").document(organizationId)
                .collection("branches").document(branchId)
                .collection("sales_returns");
    }

    @Override
    public Optional<SalesReturn> findById(String organizationId, String branchId, String salesReturnId) {
        try {
            var document = getCollection(organizationId, branchId).document(salesReturnId).get().get();
            if (document.exists()) {
                // Use ofNullable for safety in case the document data is malformed
                return Optional.ofNullable(document.toObject(SalesReturn.class));
            }
            return Optional.empty(); // Return empty if the document does not exist
        } catch (InterruptedException | ExecutionException e) {
            // In a production application, log this error properly
            throw new RuntimeException("Error finding sales return with ID: " + salesReturnId, e);
        }
    }

    @Override
    public List<SalesReturn> findAllByBranchId(String organizationId, String branchId) {
        try {
            // Get all documents from the specific branch's sales_returns collection
            var documents = getCollection(organizationId, branchId).get().get().getDocuments();

            // Use a modern Java Stream to map the Firestore documents to your model class
            return documents.stream()
                    .map(doc -> doc.toObject(SalesReturn.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            // Log this error in a real application
            throw new RuntimeException("Error finding all sales returns for branch: " + branchId, e);
        }
    }

    @Override
    public void saveInTransaction(Transaction transaction, String organizationId, String branchId, SalesReturn salesReturn) {
        var docRef = getCollection(organizationId, branchId).document(salesReturn.getSalesReturnId());
        // Add the operation to the batch. The service layer will handle the commit.
        transaction.set(docRef, salesReturn);
    }


}
