package com.cosmicdoc.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseReturnItem {
    private String medicineId;
    private String batchNo;
    private int returnQuantity;
    // --- FINANCIALS FOR THIS LINE ITEM ---
    private double costAtTimeOfPurchase; // The unit cost from the original purchase
    private double lineItemReturnValue; // Calculated: cost * returnQuantity
}
