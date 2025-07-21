package com.cosmicdoc.common.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

public class Payment {
    @JsonProperty("paymentId")
    private String id;
    private String orderId;
    private BigDecimal amount;
    @JsonProperty("payment_gateway")
    private String paymentMethod;
    private String status;
    @JsonProperty("transaction_id")
    private String transactionId;
    @JsonProperty("payment_date")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime paymentDate;
    @JsonProperty("updated_at")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;

    // Nested PaymentDetails class
    public static class PaymentDetails {
        private String last4;
        private String brand;
        private String expiryMonth;
        private String expiryYear;

        // Getters and Setters
        public String getLast4() { return last4; }
        public void setLast4(String last4) { this.last4 = last4; }
        
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        
        public String getExpiryMonth() { return expiryMonth; }
        public void setExpiryMonth(String expiryMonth) { this.expiryMonth = expiryMonth; }
        
        public String getExpiryYear() { return expiryYear; }
        public void setExpiryYear(String expiryYear) { this.expiryYear = expiryYear; }
    }

    // Getters and Setters
    @JsonProperty("paymentId")
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPaymentGateway() { return paymentMethod; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
