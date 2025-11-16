package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorefrontOrder {
    @DocumentId
    private String orderId;
    private String organizationId;
    private String branchId; // The branch that will fulfill the order

    // --- Customer Information ---
    // Could be a registered patientId or just details for a guest checkout
    private String patientId;
    private Map<String, String> customerInfo; // { name, email, phone }
    private Map<String, String> shippingAddress;

    // --- Financials ---
    private String paymentId; // Link to the PaymentOrder
    private double grandTotal;
    // ... other financial totals ...

    // --- Status & Auditing ---
    private String status; // "PENDING_PAYMENT", "PENDING_CONFIRMATION", "SHIPPED", "DELIVERED", "CANCELLED"
    private Timestamp createdAt;

    private List<SaleItem> items;
}
