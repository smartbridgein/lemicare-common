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
public class Supplier {
    @DocumentId
    private String supplierId;
    private String name;
    private String gstin; // GST Identification Number
    private String contactPerson;
    private String mobileNumber;
    private String email;
    private String address;
    private Timestamp createdAt;
    private String createdBy; // userId of the user who added the supplier
    private String status; // "ACTIVE", "INACTIVE"
    private String drugLicenseNumber;
    private double balance;
    private double outstandingBalance;
}