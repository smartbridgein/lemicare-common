package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineBatch {
    @DocumentId
    private String batchId;
    private String batchNo; // The batch number is the unique ID
    private Timestamp expiryDate;
    private int quantityAvailable; // Quantity available in this specific batch
    private double purchaseCost;
    private double mrp; // Maximum Retail Price
    private String sourcePurchaseId;

}