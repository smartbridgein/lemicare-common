package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.Purchase;
import com.google.cloud.firestore.Transaction;
import com.google.cloud.firestore.WriteBatch;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface PurchaseRepository  {
    // Note context: orgId and branchId
    Optional<Purchase> findById(String organizationId, String branchId, String purchaseId);
    List<Purchase> findAllByBranchId(String organizationId, String branchId);
    // For transactional saves during the purchase process
    void saveInTransaction(WriteBatch batch, String organizationId, String branchId, Purchase purchase);
    /**
     * Adds a save operation for a Purchase entity to an existing Transaction.
     * The repository will extract the necessary context (orgId) from the
     * purchase object itself to determine the correct storage path.
     *
     * @param transaction The active Firestore Transaction object.
     * @param purchase The complete Purchase object to save.
     */
    void saveInTransaction(Transaction transaction, Purchase purchase);

    Optional<Purchase> findById(Transaction transaction, String orgId, String branchId, String purchaseId)
            throws ExecutionException, InterruptedException;

    void deleteByIdInTransaction(Transaction transaction, String orgId, String branchId, String purchaseId);
    List<Purchase> findAllBySupplierId(String organizationId, String branchId, String supplierId);
}