// PurchaseReturn.java
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
public class PurchaseReturn {
    @DocumentId
    private String purchaseReturnId;
    private String organizationId;
    private String branchId;
    private String supplierId;
    private String supplierName;
    private Timestamp returnDate;
    private String originalPurchaseId; // Link to the original purchase for traceability
    private String reason;
    private String createdBy; // userId
    // --- FINANCIAL SUMMARY OF THE RETURN ---
    private double totalReturnedAmount; // The total value of goods being returned
    private List<PurchaseReturnItem> items;
}

