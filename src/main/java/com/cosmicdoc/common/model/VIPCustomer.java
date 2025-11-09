package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VIPCustomer implements PersistableEntity {

    @DocumentId
    private String id; // This is the customerId from the 'Customers' collection (and identities.identityId)

    // VIP Program Specific Details
    private String vipTier; // e.g., "GOLD", "SILVER", "PLATINUM"
    private VIPStatus vipStatus; // e.g., ACTIVE, PENDING_UPGRADE, EXPIRED, FROZEN (Enum recommended)

    // VIP Performance Metrics
    private Double totalPurchaseAmount; // Lifetime total amount spent as a VIP
    private Integer totalPurchaseCount; // Total number of purchases as a VIP
    private Timestamp lastPurchaseDate; // Date of their most recent purchase as a VIP
    private Timestamp memberSince; // The date they achieved VIP status or joined the VIP program

    // Preferred / Primary Location for VIP Benefits (optional, if VIP benefits are location-specific)
    // These might be derived from a default address in the Customers collection or set specifically for VIP perks.
    private String preferredStoreCode;
    private String preferredStoreName;
    private String preferredAreaCode;
    private String preferredAreaName;
    private String preferredRegion;
    private String preferredCountry;

    // Internal Notes / Metadata
    private String notes; // Any specific internal notes about this VIP customer's program membership

    // Audit Fields for this specific VIP record
    private Timestamp createdAt; // When this VIP *profile* was created in this collection
    private String createdBy;
    private Timestamp updatedAt; // Last time this VIP profile was updated
    private String updatedBy;

    // --- PersistableEntity Implementation ---
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    // You might also want to override equals() and hashCode() based on 'id'
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VIPCustomer that = (VIPCustomer) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}