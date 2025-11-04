package com.erp.controller;

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

import com.erp.dto.InvoiceRequest;
import com.erp.dto.PaymentRequest;
import com.erp.service.InvoiceService;

import entity.Invoice;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin
public class InvoiceController {

    @Autowired private InvoiceService invoiceService;

    // ✅ List all invoices
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(invoiceService.findAll());
    }

    // ✅ Get invoice by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return invoiceService.getById(id);
    }

    // ✅ Create invoice with items
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Invoice createInvoice(@RequestBody InvoiceRequest request) {
        return invoiceService.createInvoice(request);
    }

    // ✅ Update invoice (status/edit)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody InvoiceRequest req) {
        return invoiceService.updateInvoice(id, req);
    }

    // ✅ Delete invoice
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return invoiceService.delete(id);
    }

    // ✅ Get invoices for a customer
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> byCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(invoiceService.findByCustomer(customerId));
    }

    // ✅ Get invoices by status
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> byStatus(@PathVariable String status) {
        return ResponseEntity.ok(invoiceService.findByStatus(status));
    }

    // ✅ Record invoice payment (partial / full)
    @PutMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> pay(@PathVariable Long id, @RequestBody PaymentRequest req) {
        return invoiceService.recordPayment(id, req);
    }

    // ✅ Summary (total sales, unpaid)
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> summary() {
        return ResponseEntity.ok(invoiceService.getSummary());
    }
}
