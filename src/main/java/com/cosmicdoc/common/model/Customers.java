package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName; // Import this for mapping
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

    // --- NEW: Field to map the "types" field from Firestore ---
    // @PropertyName tells Firestore's CustomClassMapper to map the database field "types"
    // to this Java field named 'customerTypes'.
    // This resolves the "No setter/field for types found" warning.
    @PropertyName("types")
    private List<String> customerTypes;


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
        // This method now returns the value from the 'customerTypes' field if it's set.
        // It falls back to Collections.singletonList("CUSTOMER") if 'customerTypes' is null or empty.
        // This ensures the PersonProfile contract is met and Firestore data is used if available.
        if (customerTypes != null && !customerTypes.isEmpty()) {
            return customerTypes;
        }
        return Collections.singletonList("CUSTOMER"); // Default/fallback type
    }

    @Override
    public boolean hasType(String typeToCheck) {
        // This method now checks against the 'customerTypes' field first.
        if (customerTypes != null) {
            return customerTypes.contains(typeToCheck);
        }
        return "CUSTOMER".equals(typeToCheck); // Fallback if no specific types are set
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