// Purchase.java
package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {
    @DocumentId
    private String purchaseId;

    // --- CONTEXT FIELDS (CRUCIAL FOR MULTI-TENANCY) ---
    /**
     * The ID of the organization (hospital) this purchase belongs to.
     * This is the primary field for data isolation and querying.
     */
    private String organizationId;

    /**
     * The ID of the branch within the organization that received this inventory.
     */
    private String branchId;

    private String supplierId;
    private Timestamp invoiceDate;
    private String referenceId;

    // --- FINANCIAL FIELDS ---
    /**
     * The total value of all items before any taxes are applied.
     * Calculated as: sum(item.purchaseCost * item.paidQuantity)
     */
    private double totalTaxableAmount;
    private double totalDiscountAmount; // Store total discount

    /**
     * The total amount of tax (e.g., GST) applied to the entire purchase.
     */
    private double totalTaxAmount;

    /**
     * The grand total of the invoice.
     * Calculated as: totalTaxableAmount + totalTaxAmount
     */
    private double totalAmount;

    // --- AUDIT FIELDS ---
    private String createdBy; // userId of the user who recorded the purchase
    private Timestamp createdAt;

    private GstType gstType;

    private double amountPaid;
    private double dueAmount;
    private PaymentStatus paymentStatus; // PENDING, PARTIALLY_PAID, PAID

    private AdjustmentType overallAdjustmentType;
    private double overallAdjustmentValue;
    private double calculatedOverallAdjustmentAmount; // The final monetary value of the adjustment

    private List<PurchaseItem> items;
}

