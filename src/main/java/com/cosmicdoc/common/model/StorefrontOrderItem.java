package com.cosmicdoc.common.model;

import com.cosmicdoc.common.model.GstType;
import com.cosmicdoc.common.model.TaxComponent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A helper class (POJO) representing a single line item within a StorefrontOrder.
 * It is NOT a Firestore collection itself but is stored as an object within the
 * 'items' array of a StorefrontOrder document.
 *
 * This class captures a snapshot of all relevant product and financial data
 * at the time of sale for permanent, auditable record-keeping.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorefrontOrderItem {

    // --- Product Identification ---
    private String productId; // The medicineId from the inventory
    private String productName; // Denormalized name at time of sale
    private String sku; // Denormalized SKU at time of sale

    // --- Quantity ---
    private int quantity;

    // --- Financial Snapshot ---
    private double mrpPerItem; // The MRP (unit price) at the time of sale
    private double discountPercentage; // The discount % applied to this item

    // --- Calculated Financial Values for this Line Item ---
    private double lineItemGrossMrp;      // (mrpPerItem * quantity)
    private double lineItemDiscountAmount; // (grossMrp * discountPercentage)
    private double lineItemNetAfterDiscount; // (grossMrp - discountAmount)
    private double lineItemTaxableAmount;
    private double lineItemTaxAmount;
    private double lineItemTotalAmount;     // The final price for this line (Net After Discount)

    // --- Tax Details Snapshot ---
    private GstType gstType; // How tax was calculated (INCLUSIVE/EXCLUSIVE/NON_GST)
    private String taxProfileId;
    private double taxRateApplied;
    private String hsn;
    private List<TaxComponent> taxComponents; // Breakdown of the tax (e.g., CGST, SGST)
}
