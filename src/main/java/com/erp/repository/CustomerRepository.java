package com.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	   // Search by phone/email (useful for invoice lookups)
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhone(String phone);

    // Search by name (for dropdown suggestions)
    List<Customer> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT COUNT(c) FROM Customer c")
    Long getTotalCustomerCount();
	
}