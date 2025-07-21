package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.PurchaseReturn;
import com.google.cloud.firestore.Transaction;
import com.google.cloud.firestore.WriteBatch;

import java.util.List;
import java.util.Optional;

public interface PurchaseReturnRepository  {
    Optional<PurchaseReturn> findById(String organizationId, String branchId, String purchaseReturnId);
    void saveInTransaction(Transaction transaction, String organizationId, String branchId, PurchaseReturn purchaseReturn);
    List<PurchaseReturn> findAllByBranchId(String organizationId, String branchId);
}
