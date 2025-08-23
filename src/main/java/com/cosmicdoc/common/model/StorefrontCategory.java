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
public class StorefrontCategory {
    @DocumentId
    private String categoryId;
    private String organizationId;
    private String name;        // e.g., "Pain Relief"
    private String slug;        // e.g., "pain-relief" (for clean URLs)
    private String description;
    private String imageUrl;    // Optional image for the category page
    private String parentCategoryId; // For creating sub-categories (e.g., "Pain Relief" -> "Headache")
    private int displayOrder;
}
