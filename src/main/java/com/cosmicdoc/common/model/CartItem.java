package com.cosmicdoc.common.model;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    @DocumentId
    private String cartItemId;
    private String orgId;
    private String cartId;
    private String productId;
    private String productName;
   // private String productImageUrl;
    private double priceAtAddToCart;
    private int quantity;
    private double itemTotalPrice; // priceAtAddToCart * quantity
    private Timestamp addedAt;
    private Timestamp lastModifiedAt;
    private String sku;
}
