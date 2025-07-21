package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.Medicine;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Transaction;
import com.google.cloud.firestore.WriteBatch;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface MedicineRepository  {
    // Note: All methods require orgId and branchId to build the correct path.
    Medicine save(String organizationId, String branchId, Medicine medicine);
    Optional<Medicine> findById(String organizationId, String branchId, String medicineId);
    List<Medicine> findAllByBranchId(String organizationId, String branchId);
    void updateStockInTransaction(Transaction batch, String organizationId, String branchId, String medicineId, int quantityChange);
    void deleteById(String organizationId, String branchId, String medicineId);
    public void updateStockInTransaction(WriteBatch batch, String organizationId, String branchId, String medicineId, int quantityChange);
    List<DocumentSnapshot> getAll(Transaction transaction, String orgId, String branchId, List<String> medicineIds) throws ExecutionException, InterruptedException;
    List<Medicine> findAllByIds(String orgId, String branchId, List<String> medicineIds);
    Optional<Medicine> findById(Transaction transaction, String orgId, String branchId, String medicineId) throws ExecutionException, InterruptedException;
    Optional<Medicine> findByNameIgnoreCaseExcludingId(String orgId, String branchId, String name, String excludeMedicineId);
    boolean existsByTaxProfileId(String orgId, String taxProfileId);
    /**
     * Permanently deletes a medicine document and its associated sub-collections (like batches).
     *
     * @param organizationId The ID of the organization.
     * @param branchId The ID of the branch.
     * @param medicineId The ID of the medicine to delete.
     */
    void deleteByIdHard(String organizationId, String branchId, String medicineId);
}