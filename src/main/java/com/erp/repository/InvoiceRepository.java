package com.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
	List<Invoice> findAll();
}