package com.cosmicdoc.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleItem {
    private String medicineId;
    private String productName;
    //private String batchNo; // The batch this was sold from
    private List<BatchAllocation> batchAllocations;
    private int quantity;
    private double salePrice; // Per unit
    private double discountAmount;
    private String taxProfileId;
    private double taxAmount;

    private double mrpPerItem; // <-- ADDED: The MRP at the time of sale
    private double discountPercentage; // <-- ADDED: The discount % applied
    private double lineItemDiscountAmount; // <-- ADDED: The calculated discount
    private double lineItemTaxableAmount; // <-- ADDED
    private double lineItemTotalAmount;
    private double taxRateApplied;
    private int returnedQuantity = 0;
    private String sku;
    private String hsn;

}
