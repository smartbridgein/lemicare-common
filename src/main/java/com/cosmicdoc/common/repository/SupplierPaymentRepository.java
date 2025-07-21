package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.SupplierPayment;
import com.google.cloud.firestore.Transaction;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Repository interface for managing SupplierPayment entities.
 */
public interface SupplierPaymentRepository {

    /**
     * Adds a save operation for a new SupplierPayment to an existing Transaction.
     * This is used within a larger transaction, like creating a purchase with an
     * initial payment.
     *
     * @param transaction The active Firestore Transaction object.
     * @param organizationId The ID of the organization.
     * @param supplierId The ID of the supplier receiving the payment.
     * @param payment The SupplierPayment object to save.
     */
    void saveInTransaction(Transaction transaction, String organizationId, String supplierId, SupplierPayment payment);

    List<SupplierPayment> findAllBySupplierId(String organizationId, String supplierId);
    List<SupplierPayment> findAllByPurchaseInvoiceId(String organizationId, String supplierId, String purchaseInvoiceId);
    void deleteAllByPurchaseIdInTransaction(Transaction transaction, String orgId, String supplierId, String purchaseId) throws ExecutionException, InterruptedException;
}