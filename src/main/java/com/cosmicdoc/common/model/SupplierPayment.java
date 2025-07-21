package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupplierPayment {
    @DocumentId
    private String paymentId;
    private String purchaseInvoiceId; // Optional: Link to a specific invoice
    private Timestamp paymentDate;
    private double amountPaid;
    private PaymentMode paymentMode;
    private String referenceNumber;
    private String createdBy; // userId
}
