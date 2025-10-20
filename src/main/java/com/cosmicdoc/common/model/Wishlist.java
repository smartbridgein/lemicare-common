package com.cosmicdoc.common.model;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor // Keep for Firestore deserialization
@AllArgsConstructor // Keep for builder
public class Wishlist {
    @DocumentId
    private String customerId;
    private String organizationId;

    // Ensure this is ALWAYS initialized.
    // @Builder.Default handles the builder.
    // We'll also ensure it's not null in the service/constructor.
    @Builder.Default
    private List<WishlistItem> items = new ArrayList<>();


    // Helper method to check if an item exists
    public boolean containsProduct(String productId) {
        // No need for null check here if 'items' is guaranteed non-null
        return items.stream().anyMatch(item -> item.getProductId().equals(productId));
    }
}