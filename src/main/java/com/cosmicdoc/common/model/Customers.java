package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customers implements PersistableEntity, PersonProfile {

    @DocumentId
    private String customerId;

    private String email;

    private String displayName;

    private String mobileNumber;

    private CustomerStatus status; // ACTIVE, INACTIVE, GUEST

    private List<Address> addresses; // Assuming you have an Address POJO

    private Timestamp createdAt;

    private Timestamp lastLoginAt;

    private String hashedPassword; // Consider if this should be here or only in Identities

    // Optional link if this customer also has a B2B user profile (e.g., a doctor who is also a patient)
    private String linkedUserId; // reference to Users.userId

    // List of Organization IDs this customer is a member of (e.g., patient of these clinics)
    private List<String> organizations;

    // --- PersistableEntity Implementation ---
    @Override
    public String getId() {
        return customerId;
    }

    @Override
    public void setId(String id) {
        this.customerId = id;
    }

    // --- PersonProfile Implementation ---
    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public List<String> getTypes() {
        // A Customer entity is typically just of type "CUSTOMER".
        // If a customer can have other specific types, you would add a 'private List<String> type;' field
        // to this class and return that.
        return Collections.singletonList("CUSTOMER");
    }

    @Override
    public boolean hasType(String typeToCheck) {
        // Since we hardcode "CUSTOMER" as the type, we check against that.
        // If you had a 'type' field, you'd check this.type.contains(typeToCheck);
        return "CUSTOMER".equals(typeToCheck);
    }

    // --- HashCode and Equals based on customerId ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customers customers = (Customers) o;
        return Objects.equals(customerId, customers.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }
}
