package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.MedicineBatch;
import com.google.cloud.firestore.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface MedicineBatchRepository {
    public void saveInTransaction(Transaction transaction, String orgId, String branchId, String medicineId, MedicineBatch batch);
    List<MedicineBatch> findAvailableBatches(Transaction transaction, String orgId, String branchId, String medicineId)
            throws ExecutionException, InterruptedException;
    void updateStockInTransaction(Transaction transaction, String orgId, String branchId, String medicineId, String batchId, int quantityChange);
    List<MedicineBatch> findAllBatchesForMedicine(String orgId, String branchId, String medicineId);
    Optional<MedicineBatch> findByBatchNo(Transaction transaction, String orgId, String branchId, String medicineId, String batchNo)
            throws ExecutionException, InterruptedException;

    public void deleteByIdInTransaction(Transaction transaction, String orgId, String branchId, String medicineId, String batchId) ;
}
