package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItem {
    private String medicineId;
    private String batchNo;
    private Timestamp expiryDate;

    // Store all quantity details
    private int packQuantity;
    private int freePackQuantity;
    private int itemsPerPack;
    private int totalReceivedQuantity; // Calculated: (pack + free) * itemsPerPack

    // Store all financial steps for this line item
    private double purchaseCostPerPack;
    private double discountPercentage;
    private double lineItemDiscountAmount;
    private double lineItemTaxableAmount;
    private double lineItemTaxAmount;
    private double lineItemTotalAmount; // Taxable - Discount + Tax
    private double mrpPerItem;

    // Store tax details used for this line item
    private String taxProfileId;
    private double taxRateApplied;
    private List<TaxComponent> taxComponents;
}