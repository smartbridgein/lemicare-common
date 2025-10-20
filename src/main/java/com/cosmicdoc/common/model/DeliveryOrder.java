package com.cosmicdoc.common.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date; // Firestore's @ServerTimestamp works with java.util.Date

/**
 * Represents a delivery order document to be stored in a Firestore collection.
 * This class uses Firestore-specific annotations, not JPA.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryOrder {

    /**
     * The unique identifier for the delivery.
     * The @DocumentId annotation tells Firestore to use this field
     * as the unique ID for the document in the collection.
     */
    @DocumentId
    private String id;

    // --- Core Identifiers ---
    private String orderId;
    private String organizationId;
    private String branchId;
    private String customerId;

    // --- Partner Information ---
    private String partnerName;
    private String partnerTrackingId; // This should be indexed in Firestore for lookups awbcode

    // --- Status and Details ---
    private DeliveryStatus status; // Enums are stored as Strings by default, which is perfect.
    private String pickupAddress;
    private String dropoffAddress;
    private String recipientName;
    private String recipientPhone;
    private BigDecimal deliveryFee;
    private String notes;
    private int shipmentId;
    private int courierId;

    // --- Timestamps for Auditing ---

    /**
     * The @ServerTimestamp annotation tells Firestore to automatically populate this field
     * with the server-side commit time when the document is first created.
     * Note: This field will be null until the object is persisted.
     */
    @ServerTimestamp
    private Date createdAt;

    /**
     * For updates, you'll need to manage this field manually in your service logic
     * or use another @ServerTimestamp and clever update rules.
     * A simpler approach is to set it programmatically before saving.
     */
    private Date updatedAt;

    // Timestamps for specific events, set manually in your service
    private Date pickedUpAt;
    private Date deliveredAt;
    private Date cancelledAt;


}