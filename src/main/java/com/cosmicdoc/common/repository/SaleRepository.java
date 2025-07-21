package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.Sale;
import com.google.cloud.firestore.Transaction;
import com.google.cloud.firestore.WriteBatch;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SaleRepository  {
    Optional<Sale> findById(String organizationId, String branchId, String saleId);
    List<Sale> findAllByBranchId(String organizationId, String branchId);
    // For transactional saves during the sales process
    void saveInTransaction(WriteBatch batch, String organizationId, String branchId, Sale sale);
    public void saveInTransaction(Transaction transaction, Sale sale);
    Optional<Sale> findById(Transaction transaction, String orgId, String branchId, String saleId)
            throws java.util.concurrent.ExecutionException, java.lang.InterruptedException;

    List<Sale> findAllByBranchIdAndDate(String orgId, String branchId, LocalDate date);

    void deleteByIdInTransaction(Transaction transaction, String orgId, String branchId, String saleId);
}
