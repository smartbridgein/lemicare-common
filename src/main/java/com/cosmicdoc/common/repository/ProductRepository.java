package com.cosmicdoc.common.repository;

import com.cosmicdoc.common.model.Product;
import java.util.List;
import java.math.BigDecimal;

public interface ProductRepository extends BaseRepository<Product, String> {
    List<Product> findByCategory(String category);
    List<Product> findByPriceLessThan(BigDecimal price);
    List<Product> findByPriceGreaterThan(BigDecimal price);
    List<Product> findByStockQuantityLessThan(int quantity);
    List<Product> findByNameContaining(String name);
}
