package com.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.StockMovement;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
}