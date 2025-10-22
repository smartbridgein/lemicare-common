package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
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

    @Override
    public List<String> getTypes() {
        // A User entity is typically a "STAFF" type in your B2B context.
        // If a User can have other specific types (e.g., "SUPER_ADMIN_USER", "EXTERNAL_CONSULTANT"),
        // you would add a 'private List<String> type;' field to this class and return that.
        return Collections.singletonList("STAFF"); // Default to STAFF
    }

    @Override
    public boolean hasType(String typeToCheck) {
        // Since we hardcode "STAFF" as the type, we check against that.
        return "STAFF".equals(typeToCheck);
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