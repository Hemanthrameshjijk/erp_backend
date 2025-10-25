package com.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
