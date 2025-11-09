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
    private String userId; // Unique ID for this B2B user profile (links to Identities.identityId)

    private String email; // Denormalized from Identities for queryability
    private String displayName;
    private String mobileNumber; // Denormalized from Identities for queryability

    private String hashedPassword;

    private UserStatus status; // ACTIVE, PENDING_VERIFICATION, DISABLED

    // List of Organization IDs this user is associated with (e.g., Staff for clinics, Salesperson for retail orgs)
    private List<String> organizations;

    private Timestamp createdAt;
    private Timestamp lastLoginAt; // Last time this specific user profile was actively used/updated

    private String DOB;
    private String gender;

    // This field indicates the specific roles/types of this B2B user (e.g., "STAFF", "DOCTOR", "SALESPERSON", "MANAGER")
    @PropertyName("types")
    private List<String> userTypes;

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
        return userTypes != null ? userTypes : Collections.emptyList();
    }

    @Override
    public boolean hasType(String typeToCheck) {
        return userTypes != null && userTypes.contains(typeToCheck);
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

    // --- Utility Methods for 'userTypes' list ---
    public void addType(String newType) {
        if (this.userTypes == null) {
            this.userTypes = new ArrayList<>();
        }
        if (!this.userTypes.contains(newType)) {
            this.userTypes.add(newType);
        }
    }

    public void removeType(String typeToRemove) {
        if (this.userTypes != null) {
            this.userTypes.remove(typeToRemove);
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