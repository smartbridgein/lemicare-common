package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Salesperson implements PersistableEntity {

    @DocumentId
    private String id; // This will be the ID of the associated User document (FK to Users.id)

    private String userId;

    private String name;
    private String briefCode;
    // Job-specific identifiers and status
    private String jobNumber; // Unique identifier for the salesperson's employment record
    private String employmentStatus; // e.g., "active", "on_leave", "terminated" (distinct from user account status)

    // Organizational Role & Hierarchy (potentially denormalized or linked for easier queries)
    private String organizationId; // FK to Organization.id - Salesperson belongs to one primary organization
    private String branchId; // FK to Branch.id (optional if not branch-specific, or derived from OrganizationMember)

    // Geographical Assignment - specific to the salesperson's operational scope
    private String storeCode;
    private String storeName;
    private String areaCode;
    private String areaName;
    private String region;
    private String country;

    // Employment Details (specific to this role within the organization)
    private String employeeType; // "full-time", "part-time", "contract"
    private Timestamp joinDate;
    private Timestamp terminationDate; // If applicable
    private String jobTitle; // e.g., "Sales Associate", "District Sales Manager"
    private String department; // e.g., "Retail Sales", "Corporate Sales"

    // Compensation & Performance
    private Double commissionRate;
    private Double targetSales;
    private Double currentSales; // Current period sales, might be a computed or aggregated field

    // Role-specific permissions/capabilities
    private Boolean isStoreManager; // Specific role capability
    private Boolean canAccessPOS;
    private Boolean canProcessReturns;
    private Boolean canApplyDiscounts;

    // Audit fields (specific to the Salesperson document's lifecycle)
    private Timestamp createdAt;
    private String createdBy;
    private Timestamp updatedAt;
    private String updatedBy;

    // Additional metadata
    private String notes; // Any specific notes about this salesperson's profile/performance
}
