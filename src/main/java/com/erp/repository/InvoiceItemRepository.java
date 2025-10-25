package com.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.InvoiceItem;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
}