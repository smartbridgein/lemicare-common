package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Identities implements PersistableEntity, PersonProfile {

    @DocumentId
    private String identityId;

    private String email;

    private String passwordHash;

    private IdentityStatus status; // ACTIVE, DISABLED, LOCKED, PENDING_VERIFICATION

    // This field stores the types (e.g., "STAFF", "CUSTOMER") for this identity
    private List<String> type;

    private String linkedUserId;     // If this identity belongs to a B2B user (staff), reference Users.userId
    private String linkedCustomerId; // If this identity belongs to a B2C customer, reference Customers.customerId

    private Timestamp createdAt;

    private Timestamp lastLoginAt;

    private String mobileNumber;

    // --- PersistableEntity Implementation ---
    @Override
    public String getId() {
        return identityId;
    }

    @Override
    public void setId(String id) {
        this.identityId = id;
    }

    // --- PersonProfile Implementation ---
    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getDisplayName() {
        // Identities itself might not have a direct display name.
        // It's often derived from the linked user/customer.
        // For simplicity here, we can default to email or a specific name if available.
        // You might consider adding a 'primaryDisplayName' field to Identities if needed.
        return this.email; // Default to email as a placeholder
    }

    @Override
    public List<String> getTypes() {
        // Return the actual list of types for this identity.
        // Ensure it's never null by returning an empty list if no types are set.
        return type != null ? type : Collections.emptyList();
    }

    @Override
    public boolean hasType(String typeToCheck) {
        return type != null && type.contains(typeToCheck);
    }

    // --- Utility Methods for 'type' list management ---
    public void addType(String newType) {
        if (this.type == null) {
            this.type = new ArrayList<>();
        }
        if (!this.type.contains(newType)) {
            this.type.add(newType);
        }
    }

    public void removeType(String typeToRemove) {
        if (this.type != null) {
            this.type.remove(typeToRemove);
        }
    }

    // Lombok's @Data will generate the default `getType()` and `setType()` for the 'type' field.
    // This is fine as our interface uses `getTypes()` (plural).

    // --- HashCode and Equals based on identityId (important for collections) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identities that = (Identities) o;
        return Objects.equals(identityId, that.identityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identityId);
    }
}
