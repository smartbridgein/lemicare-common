package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.Supplier;
import com.google.cloud.firestore.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface SupplierRepository {
    List<Supplier> findAllByOrganizationId(String organizationId); // All suppliers for the org
    Supplier save(String organizationId, Supplier supplier);
    void deleteById(String organizationId, String supplierId);
    Optional<Supplier> findById(String organizationId, String supplierId);
    boolean existsById(Transaction transaction, String orgId, String supplierId) throws ExecutionException, InterruptedException;
    void updateBalanceInTransaction(Transaction transaction, String orgId, String supplierId, double amountChange);

}
