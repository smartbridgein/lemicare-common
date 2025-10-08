package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationMember implements PersistableEntity {
    @DocumentId
    private String membershipId; // Unique ID for this specific membership record

    private String organizationId; // FK to Organizations (which organization this membership is for)

    // --- Identification of the member ---
    // At least one of these should be present.
    // If you have a Patient entity distinct from Customer, you'd add: private String patientId;
    private String userId;      // FK to Users (if this member is a B2B user/staff)
    private String customerId;  // FK to Customers (if this member is a B2C customer/client)

    // --- Membership Details ---
    private OrganizationRole role;       // The primary role of this member within THIS organization (e.g., "ADMIN", "DOCTOR", "STAFF", "PATIENT", "CLIENT")
    private String accessType;  // A more granular access level or category (e.g., "FULL_ACCESS", "READ_ONLY", "LIMITED_PATIENT_VIEW"). Can be derived from role.

    private OrganizationMemberStatus status; // Status of *this specific membership* (e.g., ACTIVE, INVITED, PENDING, SUSPENDED, LEFT)

    private List<Permission> permissions; // Detailed permissions, potentially tied to branches.

    // --- Timestamps (Highly Recommended) ---
    private Timestamp joinedAt;         // When this member joined this organization (or when membership record was created)
    private Timestamp lastActivityAt;   // Last time this member actively interacted within this organization's context (e.g., login, action)
    private Timestamp lastModifiedAt;   // Last time *this membership record* was updated (e.g., role change, status change)

    // --- Audit Fields (Highly Recommended) ---
    private String createdBy;         // User ID (or system identifier) who created this membership record
    private String lastModifiedBy;    // User ID (or system identifier) who last modified this membership record

    // --- Optional but potentially useful fields ---
    private Timestamp validUntil;       // If memberships can expire (e.g., subscription end date, temporary access)
    private String reasonForStatusChange; // To log why a membership status was changed (e.g., "User suspended for non-payment")

    @Override
    public String getId() {
        return membershipId;
    }

    @Override
    public void setId(String membershipId) {
      this.membershipId = membershipId;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Permission {
        private String resource; // e.g., "PATIENT_RECORD", "APPOINTMENT"
        private String action;   // e.g., "READ", "WRITE", "DELETE"
        private String branchId;
        private Boolean isDefault;// Specific branch if permission is branch-scoped
    }
}
