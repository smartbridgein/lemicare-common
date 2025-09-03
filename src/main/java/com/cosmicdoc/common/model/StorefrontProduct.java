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
@NoArgsConstructor
@AllArgsConstructor
public class StorefrontProduct {
    @DocumentId
    private String productId; // Mirrors the medicineId from inventory

    private String organizationId; // For security rules and collection group queries

    private String branchId; // To link with a specific branch/store

    // --- Core CMS Fields ---
    private String richDescription; // Can store HTML or Markdown
    private String highlights;      // e.g., "Key benefits", "How to use"
    private boolean isVisible;      // Master switch to publish/unpublish from the website

    // --- Structured Image Data ---
    @Builder.Default
    private List<ImageAsset> images = new ArrayList<>();

    // --- SEO & Categorization ---
    private String categoryId; // Foreign key to the storefront_categories collection
    private String slug;       // URL-friendly version of the product name (e.g., "paracetamol-500mg")
    private List<String> tags; // Search keywords (e.g., ["fever", "painkiller", "headache"])
    private Double mrp;
    private int stockLevel;
    private String currentStatus;
    private String productName;

}
