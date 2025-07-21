package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Order;
import com.cosmicdoc.common.repository.OrderRepository;
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
public class OrderRepositoryImpl implements OrderRepository {
    private final JsonDataLoader jsonDataLoader;
    private final Map<String, Order> orderMap = new ConcurrentHashMap<>();

    public OrderRepositoryImpl(JsonDataLoader jsonDataLoader) {
        this.jsonDataLoader = jsonDataLoader;
        loadOrders();
    }

    private void loadOrders() {
        List<Order> orders = jsonDataLoader.loadData("orders.json", "orders", Order.class);
        orders.stream()
            .filter(order -> order != null && order.getId() != null)
            .forEach(order -> orderMap.put(order.getId(), order));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orderMap.values());
    }

    @Override
    public Optional<Order> findById(String id) {
        return Optional.ofNullable(orderMap.get(id));
    }

    @Override
    public Order save(Order order) {
        orderMap.put(order.getId(), order);
        return order;
    }

    @Override
    public void deleteById(String id) {
        orderMap.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return orderMap.containsKey(id);
    }

    @Override
    public List<Order> findByUserId(String userId) {
        return orderMap.values().stream()
                .filter(order -> order.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByStatus(String status) {
        return orderMap.values().stream()
                .filter(order -> order.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByPaymentStatus(String paymentStatus) {
        return orderMap.values().stream()
                .filter(order -> order.getPaymentStatus().equals(paymentStatus))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByTotalPriceGreaterThan(BigDecimal price) {
        return orderMap.values().stream()
                .filter(order -> order.getTotalPrice().compareTo(price) > 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return orderMap.values().stream()
                .filter(order -> {
                    LocalDateTime createdAt = order.getCreatedAt();
                    return !createdAt.isBefore(start) && !createdAt.isAfter(end);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByUserIdAndStatus(String userId, String status) {
        return orderMap.values().stream()
                .filter(order -> order.getUserId().equals(userId) 
                        && order.getStatus().equals(status))
                .collect(Collectors.toList());
    }
}
