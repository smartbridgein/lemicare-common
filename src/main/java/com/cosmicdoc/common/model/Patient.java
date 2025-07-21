package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @DocumentId
    private String patientId; // Unique ID for the patient within the system.

    // --- MULTI-TENANCY & CONTEXT FIELDS (ESSENTIAL) ---
    private String organizationId; // The hospital (tenant) this patient belongs to.
    private String registeredAtBranchId; // The branch where the patient was first registered.

    // --- DEMOGRAPHIC INFORMATION ---
    private String firstName;
    private String lastName;
    private Timestamp dateOfBirth; // Use Firestore's native Timestamp for querying.
    private String gender; // e.g., "Male", "Female", "Other"

    // --- CONTACT INFORMATION (Structured as a Map) ---
    private Map<String, String> contactInfo;
    // Example: { "mobileNumber": "...", "email": "...", "addressLine1": "...", "city": "...", "state": "...", "zipCode": "..." }

    // --- MEDICAL INFORMATION ---
    private String bloodGroup;
    private List<String> allergies; // A list of allergy names for easy querying.
    private List<String> chronicDiseases; // A list of chronic conditions.

    // --- AUDIT & STATUS FIELDS ---
    private Timestamp registrationDate; // When the patient was created.
    private String registeredBy; // userId of the staff who registered the patient.
    private boolean isActive; // For soft-deleting or deactivating patient records.


}
