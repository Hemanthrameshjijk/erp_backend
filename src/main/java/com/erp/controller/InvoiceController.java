package com.erp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.repository.InvoiceRepository;
import com.erp.service.InvoiceService;

import entity.Invoice;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin
public class InvoiceController {
    @Autowired  InvoiceRepository invoiceRepo;
    @Autowired private InvoiceService invoiceService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<Invoice> list() { return invoiceRepo.findAll(); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Invoice create(@RequestBody Invoice invoice) {
        // Expect invoice.items to be present with product ids & qty
        return invoiceService.createInvoice(invoice);
    }
}
