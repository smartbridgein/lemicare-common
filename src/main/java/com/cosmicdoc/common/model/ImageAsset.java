package com.cosmicdoc.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageAsset {
    private String assetId;      // A unique ID for this image asset (e.g., ULID)
    private String originalUrl;    // URL to the full-resolution image in cloud storage
    private String thumbnailUrl;   // URL to a small (e.g., 200x200) version
    private String mediumUrl;      // URL to a medium (e.g., 600x600) version
    private String largeUrl;       // URL to a large (e.g., 1200x1200) version
    private String altText;        // Crucial for accessibility (ADA) and SEO
    private int displayOrder;    // 0, 1, 2, 3... to control the gallery order
}
