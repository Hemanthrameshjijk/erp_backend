package com.erp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.dto.InvoiceRequest;
import com.erp.dto.InvoiceResponseDTO;
import com.erp.dto.InvoiceSummaryDTO;
import com.erp.dto.PaymentRequest;
import com.erp.service.InvoiceService;

import entity.Invoice;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin
public class InvoiceController {

    @Autowired private InvoiceService invoiceService;

    // ✅ Get invoice by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public InvoiceResponseDTO getById(@PathVariable Long id) {
        return invoiceService.getById(id);
    }

    // ✅ Create invoice with items
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Invoice createInvoice(@RequestBody InvoiceRequest request) {
        return invoiceService.createInvoice(request);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<InvoiceResponseDTO> list() {
        return (invoiceService.findAll());  // Use DTOs
    }
    // ✅ Update payment
    @PutMapping("/{id}")
    public InvoiceResponseDTO updatePayment(
            @PathVariable Long id,
            @RequestBody PaymentRequest request
    ) {
        return invoiceService.updatePayment(id, request);
    }

    // ✅ Get invoices for a customer
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<InvoiceResponseDTO> byCustomer(@PathVariable Long customerId) {
        return (invoiceService.findByCustomer(customerId));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<InvoiceResponseDTO> byStatus(@PathVariable String status) {
        return invoiceService.findByStatus(status);
    }


    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public InvoiceSummaryDTO summary() {
        return invoiceService.getSummary();
    }

    

}
