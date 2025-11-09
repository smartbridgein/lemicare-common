package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName; // Import this for mapping
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customers implements PersistableEntity, PersonProfile {

    @DocumentId
    private String customerId; // Unique ID for this B2C customer profile (links to Identities.identityId)

    private String email; // Denormalized from Identities for queryability
    private String displayName;
    private String mobileNumber; // Denormalized from Identities for queryability

    private CustomerStatus status; // ACTIVE, INACTIVE, GUEST

    private List<Address> addresses; // Common addresses

    private Timestamp createdAt;
    private Timestamp lastLoginAt; // Last time this specific customer profile was actively used/updated

    private String DOB;
    private String gender;

    // Optional link if this customer also has a B2B user profile (e.g., a doctor who is also a patient)
    private String linkedUserId; // reference to Users.userId

    // List of Organization IDs this customer is a member of (e.g., patient of these clinics, shopper at retail stores)
    private List<String> organizations;

    // This field indicates the specific types of this B2C customer (e.g., "CUSTOMER", "PATIENT", "VIP_CUSTOMER")
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
        return customerTypes != null ? customerTypes : Collections.emptyList();
    }

    @Override
    public boolean hasType(String typeToCheck) {
        return customerTypes != null && customerTypes.contains(typeToCheck);
    }

    // --- Utility Methods for 'organizations' list ---
    public void addOrganization(String orgId) {
        if (this.organizations == null) {
            this.organizations = new ArrayList<>();
        }
        if (!this.organizations.contains(orgId)) {
            this.organizations.add(orgId);
        }
    }

    public void removeOrganization(String orgId) {
        if (this.organizations != null) {
            this.organizations.remove(orgId);
        }
    }

    // --- Utility Methods for 'customerTypes' list ---
    public void addType(String newType) {
        if (this.customerTypes == null) {
            this.customerTypes = new ArrayList<>();
        }
        if (!this.customerTypes.contains(newType)) {
            this.customerTypes.add(newType);
        }
    }

    public void removeType(String typeToRemove) {
        if (this.customerTypes != null) {
            this.customerTypes.remove(typeToRemove);
        }
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