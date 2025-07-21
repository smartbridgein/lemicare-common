package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends BaseRepository<Order, String> {
    List<Order> findByUserId(String userId);
    List<Order> findByStatus(String status);
    List<Order> findByPaymentStatus(String paymentStatus);
    List<Order> findByTotalPriceGreaterThan(BigDecimal price);
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Order> findByUserIdAndStatus(String userId, String status);
}
