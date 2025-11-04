package com.erp.controller;

import com.erp.dto.InvoiceItemDTO;
import com.erp.service.InvoiceItemService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/invoice-items")
@RequiredArgsConstructor
public class InvoiceItemController {

    private final InvoiceItemService invoiceItemService;

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<InvoiceItemDTO> getItemsByInvoice(@PathVariable Long invoiceId) {
        return invoiceItemService.getItemsByInvoice(invoiceId);
    }

    @PostMapping
    public InvoiceItemDTO addItem(@RequestBody InvoiceItemDTO request) {
        return invoiceItemService.addItem(request);
    }

    @DeleteMapping("/{id}")
    public String deleteItem(@PathVariable Long id) {
        invoiceItemService.deleteItem(id);
        return "Item removed from invoice";
    }
}
