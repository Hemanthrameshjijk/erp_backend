package com.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.stock <= p.reorderLevel")
    List<Product> findProductsLowInStock();
    
    @Query("SELECT p FROM Product p WHERE p.stock <= p.reorderLevel")
    List<Product> getLowStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock <= p.reorderLevel")
    Integer lowStockCount();
}
