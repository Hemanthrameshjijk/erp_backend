package com.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entity.CrmNote;

@Repository
public interface CrmNoteRepository extends JpaRepository<CrmNote, Long> {
	List<CrmNote> findByCustomer_IdOrderByCreatedAtDesc(Long customerId);

}