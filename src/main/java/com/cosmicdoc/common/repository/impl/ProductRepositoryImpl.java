package com.cosmicdoc.common.repository.impl;

import com.cosmicdoc.common.model.Product;
import com.cosmicdoc.common.repository.ProductRepository;
import com.cosmicdoc.common.util.JsonDataLoader;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ProductRepositoryImpl implements ProductRepository {
    private final JsonDataLoader jsonDataLoader;
    private final Map<String, Product> productMap = new ConcurrentHashMap<>();

    public ProductRepositoryImpl(JsonDataLoader jsonDataLoader) {
        this.jsonDataLoader = jsonDataLoader;
        loadProducts();
    }

    private void loadProducts() {
        List<Product> products = jsonDataLoader.loadData("products.json", "products", Product.class);
        products.stream()
            .filter(product -> product != null && product.getProductId() != null)
            .forEach(product -> productMap.put(product.getProductId(), product));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(productMap.values());
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(productMap.get(id));
    }

    @Override
    public Product save(Product product) {
        productMap.put(product.getProductId(), product);
        return product;
    }

    @Override
    public void deleteById(String id) {
        productMap.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return productMap.containsKey(id);
    }

    @Override
    public List<Product> findByCategory(String category) {
        return productMap.values().stream()
                .filter(product -> product.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByPriceLessThan(BigDecimal price) {
        return productMap.values().stream()
                .filter(product -> product.getPrice().compareTo(price) < 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByPriceGreaterThan(BigDecimal price) {
        return productMap.values().stream()
                .filter(product -> product.getPrice().compareTo(price) > 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByStockQuantityLessThan(int quantity) {
        return productMap.values().stream()
                .filter(product -> product.getStockQuantity() < quantity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByNameContaining(String name) {
        return productMap.values().stream()
                .filter(product -> product.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
}
