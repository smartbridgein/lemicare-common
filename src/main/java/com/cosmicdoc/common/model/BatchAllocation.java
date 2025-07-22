package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAllocation {
    /**
     * The unique document ID of the MedicineBatch this stock was taken from.
     */
    private String batchId;

    /**
     * The human-readable batch number (e.g., "AB-123").
     */
    private String batchNo;

    /**
     * The quantity of units taken from this specific batch for the sale item.
     */
    private int quantityTaken;

    private Timestamp expiryDate;
}
