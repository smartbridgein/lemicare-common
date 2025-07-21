// SalesReturn.java
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
@AllArgsConstructor
@NoArgsConstructor
public class SalesReturn {
    @DocumentId
    private String salesReturnId;
    private String organizationId;
    private String branchId;
    private String patientId; // Can be null if it was an OTC sale
    private Timestamp returnDate;
    private String originalSaleId; // Link to the original sale
    private double refundAmount;
    private String createdBy; // userId
    // --- FINANCIAL SUMMARY OF THE RETURN / CREDIT NOTE ---
    private double totalReturnedMrp;
    private double totalReturnedDiscount;
    private double totalReturnedTaxable;
    private double totalReturnedTax;
    private double overallDiscountPercentage;
    private double overallDiscountAmount;
    private double netRefundAmount; // The final amount to be returned to the customer
    private PaymentMode refundMode; // How the refund was given (e.g., CASH, To Bank Account)
    private String refundReference; // Optional reference for the refund transaction
    private List<SalesReturnItem> items;
}


