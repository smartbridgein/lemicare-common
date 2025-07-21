package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.SalesReturn;
import com.google.cloud.firestore.Transaction;
import com.google.cloud.firestore.WriteBatch;

import java.util.List;
import java.util.Optional;

public interface SalesReturnRepository  {
    /**
     * Finds a specific sales return by its ID within a given branch.
     * @param organizationId The ID of the parent organization.
     * @param branchId The ID of theOf course. Here branch.
     * @param salesReturnId The ID of the sales return to find.
     * @return is the complete code for the `SalesReturnRepository` and `PurchaseReturnRepository An Optional containing the SalesReturn if found.
     */
    Optional<SalesReturn> findById(String organizationId, String branchId, String salesReturnId);
    List<SalesReturn> findAllByBranchId(String organizationId, String branchId);
    void saveInTransaction(Transaction transaction, String organizationId, String branchId, SalesReturn salesReturn);

}
