package com.cosmicdoc.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    // If Address is embedded in Customer, you might not need @DocumentId here
    // But if Address can be a sub-collection or shared, it's good to have an ID
    private String addressId; // Unique ID for this address

    private String streetLine1;
    private String streetLine2;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String country;
    private String addressType; // e.g., "DELIVERY", "BILLING", "HOME", "WORK"
    private String phoneNumber;
    private String notes; // Delivery instructions
    private boolean isDefault; // e.g., if customer has multiple addresses
    private boolean isActive; // e.g., if an address becomes invalid
    private String contactPerson;
    private String contactEmail;
}
