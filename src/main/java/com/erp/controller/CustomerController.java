package com.erp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.repository.CustomerRepository;

import entity.Customer;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin
public class CustomerController {
    @Autowired private CustomerRepository customerRepo;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<Customer> list() { return customerRepo.findAll(); }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Customer create(@RequestBody Customer c) { return customerRepo.save(c); }
    

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable Long id) {

        var customer = customerRepo.findById(id);

        if (customer.isPresent()) {
            return ResponseEntity.ok(customer.get());
        }

        return ResponseEntity.status(404)
                .body(Map.of("error", "Customer not found"));
    }
    

    // PUT /api/customers/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> updateCustomer(
            @PathVariable Long id, @RequestBody Customer updated) {

        return customerRepo.findById(id).map(c -> {
            if (updated.getName() != null) c.setName(updated.getName());
            if (updated.getEmail() != null) c.setEmail(updated.getEmail());
            if (updated.getPhone() != null) c.setPhone(updated.getPhone());
            if (updated.getAddress() != null) c.setAddress(updated.getAddress());
            
            customerRepo.save(c);
            return ResponseEntity.ok(Map.of(
                "message", "Customer updated successfully",
                "id", c.getId(),
                "name", c.getName()
            ));
        }).orElse(ResponseEntity.status(404)
            .body(Map.of("error", "Customer not found")));
    }

    // DELETE /api/customers/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        if (!customerRepo.existsById(id)) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Customer not found"));
        }
        customerRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Customer deleted"));
    }
}