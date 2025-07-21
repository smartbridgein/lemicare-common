package com.cosmicdoc.common.dto;

import com.cosmicdoc.common.model.Order;

public class OrderRequest {
    private String userId;
    private String productId;
    private Integer quantity;
    private Order.Address shippingAddress;

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public Order.Address getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(Order.Address shippingAddress) { this.shippingAddress = shippingAddress; }
}
