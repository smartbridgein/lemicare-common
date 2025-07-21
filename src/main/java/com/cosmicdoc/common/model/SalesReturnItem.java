package com.cosmicdoc.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesReturnItem {
    private String medicineId;
    private String batchNo; // Important to know which batch is being returned
    private int returnQuantity;
    private double returnPrice; // The price at which it was returned

    // --- FINANCIALS FOR THIS RETURNED LINE ITEM ---
    private double mrpAtTimeOfSale;
    private double discountPercentageAtSale;
    private double lineItemReturnValue; // The value of the returned goods for this line
    private double lineItemTaxAmount;
}

