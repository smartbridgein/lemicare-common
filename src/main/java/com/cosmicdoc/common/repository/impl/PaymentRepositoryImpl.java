package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Payment;
import com.cosmicdoc.common.repository.PaymentRepository;
import com.cosmicdoc.common.util.JsonDataLoader;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {
    private final JsonDataLoader jsonDataLoader;
    private final Map<String, Payment> paymentMap = new ConcurrentHashMap<>();

    public PaymentRepositoryImpl(JsonDataLoader jsonDataLoader) {
        this.jsonDataLoader = jsonDataLoader;
        loadPayments();
    }

    private void loadPayments() {
        List<Payment> payments = jsonDataLoader.loadData("payments.json", "payments", Payment.class);
        payments.stream()
            .filter(payment -> payment != null && payment.getId() != null)
            .forEach(payment -> paymentMap.put(payment.getId(), payment));
    }

    @Override
    public List<Payment> findAll() {
        return new ArrayList<>(paymentMap.values());
    }

    @Override
    public Optional<Payment> findById(String id) {
        return Optional.ofNullable(paymentMap.get(id));
    }

    @Override
    public Payment save(Payment payment) {
        paymentMap.put(payment.getId(), payment);
        return payment;
    }

    @Override
    public void deleteById(String id) {
        paymentMap.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return paymentMap.containsKey(id);
    }

    @Override
    public Optional<Payment> findByOrderId(String orderId) {
        return paymentMap.values().stream()
                .filter(payment -> payment.getOrderId().equals(orderId))
                .findFirst();
    }

    @Override
    public List<Payment> findByStatus(String status) {
        return paymentMap.values().stream()
                .filter(payment -> payment.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByPaymentGateway(String gateway) {
        return paymentMap.values().stream()
                .filter(payment -> payment.getPaymentGateway().equals(gateway))
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByAmountGreaterThan(BigDecimal amount) {
        return paymentMap.values().stream()
                .filter(payment -> payment.getAmount().compareTo(amount) > 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end) {
        return paymentMap.values().stream()
                .filter(payment -> {
                    LocalDateTime paymentDate = payment.getPaymentDate();
                    return !paymentDate.isBefore(start) && !paymentDate.isAfter(end);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByTransactionId(String transactionId) {
        return paymentMap.values().stream()
                .filter(payment -> payment.getTransactionId().equals(transactionId))
                .collect(Collectors.toList());
    }
}
