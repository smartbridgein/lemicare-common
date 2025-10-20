package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItem {
    private String productId;
    private Timestamp addedAt;
}
