// Sale.java
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
public class Sale {
    @DocumentId
    private String saleId;
    private String organizationId;
    private String branchId;
    private String saleType; // "PRESCRIPTION" or "OTC"
    private Timestamp saleDate;
    private String createdBy; // userId of the pharmacy staff

    // For PRESCRIPTION sales
    private String patientId;
    private String doctorId;
    private String doctorName;
    private Timestamp prescriptionDate;

    // For OTC sales
    private String walkInCustomerName;
    private String walkInCustomerMobile;


    // Financials
    private double totalTaxableAmount;
    private double totalTaxAmount;
    private double grandTotal;
    private double totalMrpAmount; // <-- ADDED
    private double totalDiscountAmount;

    private PaymentMode paymentMode;
    private String transactionReference;

    private GstType gstType; //

    private List<SaleItem> items;

    private AdjustmentType overallAdjustmentType;
    private double overallAdjustmentValue;
    private double calculatedOverallAdjustmentAmount;
    private String address;
    private String gender;
    private int age;
}


