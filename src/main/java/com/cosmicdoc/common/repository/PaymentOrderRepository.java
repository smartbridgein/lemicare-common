package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.PaymentOrder;

import java.util.Optional;

public interface PaymentOrderRepository {
    PaymentOrder save(PaymentOrder paymentOrder);

    // Find methods must be scoped to the organization
    Optional<PaymentOrder> findByRazorpayOrderId(String organizationId, String branchId, String razorpayOrderId);

    Optional<PaymentOrder> findById(String organizationId, String branchId, String orderId);
}