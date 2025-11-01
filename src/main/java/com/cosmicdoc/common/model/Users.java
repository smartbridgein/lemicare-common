package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users implements PersistableEntity, PersonProfile {

    @DocumentId
    private String userId; // Unique ID for this B2B user profile

    private String email;

    private String displayName;

    private String mobileNumber;

    private UserStatus status; // ACTIVE, PENDING_VERIFICATION, DISABLED

    // List of Organization IDs this user is associated with (e.g., Staff for these clinics)
    private List<String> organizations;

    private Timestamp createdAt;

    private Timestamp lastLoginAt;

    private String hashedPassword; // This should ideally be null/empty after migration to Identities

    private String DOB;

    private String gender;

    // --- PersistableEntity Implementation ---
    @Override
    public String getId() {
        return userId;
    }

    @Override
    public void setId(String id) {
        this.userId = id;
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

    @PropertyName("types")
    private List<String> userTypes;

    @Override
    public List<String> getTypes() {
        // This method now returns the value from the 'customerTypes' field if it's set.
        // It falls back to Collections.singletonList("CUSTOMER") if 'customerTypes' is null or empty.
        // This ensures the PersonProfile contract is met and Firestore data is used if available.
        if (userTypes != null && !userTypes.isEmpty()) {
            return userTypes;
        }
        return Collections.singletonList("STAFF"); // Default/fallback type
    }

    @Override
    public boolean hasType(String typeToCheck) {
        // This method now checks against the 'customerTypes' field first.
        if (userTypes != null) {
            return userTypes.contains(typeToCheck);
        }
        return "STAFF".equals(typeToCheck); // Fallback if no specific types are set
    }

    // --- Utility Methods for 'organizations' list (if needed) ---
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

    // --- HashCode and Equals based on userId ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return Objects.equals(userId, users.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}