package com.cosmicdoc.common.model;

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
public class Cart {
    @DocumentId
    private String cartId;
    private String orgId;
    private String userId;
    private String guestId;
    private String status; // ACTIVE, ABANDONED, CONVERTED_TO_ORDER, EXPIRED
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private LocalDateTime expiresAt; // For guest carts or abandoned carts
    private int totalItems; // Denormalized count
    private double subtotalAmount; // Denormalized sum of item prices
}
