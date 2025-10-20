package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentOrder {
    @DocumentId
    private String orderId; // Your internal system's ID (e.g., ULID)
    private String organizationId;
    private String branchId;
    private String customerId;

    private String sourceService; // "OPD", "INVENTORY", etc.
    private String sourceInvoiceId; // Your internal invoice ID (e.g., "inv_123")

    private String razorpayOrderId;
    private String razorpayPaymentId; // Populated after successful payment
    private String razorpaySignature; // Stored for auditing

    private double amount;
    private String currency; // "INR"
    private String status; // "CREATED", "ATTEMPTED", "PAID", "FAILED"

    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String createdByUserId;   // The staff member who initiated the payment
}