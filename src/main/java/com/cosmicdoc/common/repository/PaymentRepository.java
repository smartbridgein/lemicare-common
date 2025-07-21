package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.Payment;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends BaseRepository<Payment, String> {
    Optional<Payment> findByOrderId(String orderId);
    List<Payment> findByStatus(String status);
    List<Payment> findByPaymentGateway(String gateway);
    List<Payment> findByAmountGreaterThan(BigDecimal amount);
    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);
    List<Payment> findByTransactionId(String transactionId);
}
