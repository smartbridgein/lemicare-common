package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch implements PersistableEntity{
    @DocumentId
    private String branchId;
    private String organizationId;
    private String name;
    private String address;
    private String branchCode;
    private Timestamp createdAt;
    private String shiprocketPickupLocation;
    private String defaultCourierPartner;
    private Address primaryPickupAddress;
    private String gstin;

    @Override
    public String getId() {
        return branchId;
    }

    @Override
    public void setId(String branchId) {
       this.branchId = branchId;
    }
}
