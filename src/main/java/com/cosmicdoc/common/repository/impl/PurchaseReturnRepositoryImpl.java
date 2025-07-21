package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.PurchaseReturn;
import com.cosmicdoc.common.repository.PurchaseReturnRepository;
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

public class PurchaseReturnRepositoryImpl implements PurchaseReturnRepository {

   private final Firestore firestore;

   public PurchaseReturnRepositoryImpl (Firestore firestore) {
       this.firestore = firestore;
   }
    private CollectionReference getCollection(String organizationId, String branchId) {
        return firestore.collection("organizations").document(organizationId)
                .collection("branches").document(branchId)
                .collection("purchase_returns");
    }

    @Override
    public Optional<PurchaseReturn> findById(String organizationId, String branchId, String purchaseReturnId) {
        try {
            var document = getCollection(organizationId, branchId).document(purchaseReturnId).get().get();
            if (document.exists()) {
                // Use ofNullable for safety against malformed data in the database
                return Optional.ofNullable(document.toObject(PurchaseReturn.class));
            }
            return Optional.empty();
        } catch (InterruptedException | ExecutionException e) {
            // Log the error in a real application
            throw new RuntimeException("Error finding purchase return " + purchaseReturnId, e);
        }
    }

    @Override
    public void saveInTransaction(Transaction transaction, String organizationId, String branchId, PurchaseReturn purchaseReturn) {
        var docRef = getCollection(organizationId, branchId).document(purchaseReturn.getPurchaseReturnId());
        // Add the 'set' operation to the batch. The batch will be committed by the service layer.
        transaction.set(docRef, purchaseReturn);
    }

    @Override
    public List<PurchaseReturn> findAllByBranchId(String organizationId, String branchId) {
        try {
            var documents = getCollection(organizationId, branchId).get().get().getDocuments();
            return documents.stream()
                    .map(doc -> doc.toObject(PurchaseReturn.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error finding all purchase returns for branch: " + branchId,e);

        }
    }
  }


