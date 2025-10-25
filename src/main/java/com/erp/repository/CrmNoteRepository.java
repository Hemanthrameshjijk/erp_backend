package com.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.CrmNote;

public interface CrmNoteRepository extends JpaRepository<CrmNote, Long> {
}