package com.cosmicdoc.common.model;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Medicine {
    @DocumentId
    private String medicineId;

    private String name; // Corresponds to "Medicine Name"

    private String genericName; // Corresponds to "Generic"

    private String category; // Corresponds to "Group Name"

    private String manufacturer; // Corresponds to "Company"

    // Optional Fields
    private String sku; // Stock Keeping Unit (optional)
    private String hsnCode; // For GST compliance (optional)
    private String location; // e.g., "Rack 5, Shelf B" (optional)

   private String unitOfMeasurement; // e.g., "Strip", "Bottle", "Tablet"

   private Integer lowStockThreshold; // Corresponds to "Reorder Reminder" or "Low Stock Threshold"

   private String taxProfileId; // Link to a TaxProfile document

   private Double unitPrice; // Standard selling price

    private String status; // "ACTIVE" or "INACTIVE"

    private int quantityInStock;
}