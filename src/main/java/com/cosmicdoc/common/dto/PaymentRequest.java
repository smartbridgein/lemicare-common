package com.cosmicdoc.common.dto;

import java.math.BigDecimal;

public class PaymentRequest {
    private String orderId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private PaymentMethodDetails paymentMethodDetails;

    public static class PaymentMethodDetails {
        private String cardNumber;
        private String expiryMonth;
        private String expiryYear;
        private String cvv;
        private String cardHolderName;

        // Getters and Setters
        public String getCardNumber() { return cardNumber; }
        public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
        
        public String getExpiryMonth() { return expiryMonth; }
        public void setExpiryMonth(String expiryMonth) { this.expiryMonth = expiryMonth; }
        
        public String getExpiryYear() { return expiryYear; }
        public void setExpiryYear(String expiryYear) { this.expiryYear = expiryYear; }
        
        public String getCvv() { return cvv; }
        public void setCvv(String cvv) { this.cvv = cvv; }
        
        public String getCardHolderName() { return cardHolderName; }
        public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public PaymentMethodDetails getPaymentMethodDetails() { return paymentMethodDetails; }
    public void setPaymentMethodDetails(PaymentMethodDetails paymentMethodDetails) { this.paymentMethodDetails = paymentMethodDetails; }
}
