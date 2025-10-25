package com.erp.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.repository.InvoiceRepository;
import com.erp.repository.ProductRepository;

import entity.Invoice;
import entity.InvoiceItem;
import entity.Product;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepo;
    private final ProductRepository productRepo;
    private final ProductService productService;

    public InvoiceService(InvoiceRepository invoiceRepo, ProductRepository productRepo, ProductService productService) {
        this.invoiceRepo = invoiceRepo;
        this.productRepo = productRepo;
        this.productService = productService;
    }

    @Transactional
    public Invoice createInvoice(Invoice invoice) {
        // generate invoiceNo
        invoice.setInvoiceNo("INV-" + UUID.randomUUID().toString().substring(0,8).toUpperCase());

        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : invoice.getItems()) {
            Product product = productRepo.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProduct().getId()));

            BigDecimal unit = item.getUnitPrice() == null ? product.getSellPrice() : item.getUnitPrice();
            item.setUnitPrice(unit);
            BigDecimal subtotal = unit.multiply(BigDecimal.valueOf(item.getQty()));
            item.setSubtotal(subtotal);
            total = total.add(subtotal);

            // reduce stock
            product.setStock(product.getStock() - item.getQty());
            productRepo.save(product);

            // create stock movement (OUT)
            productService.changeStock(product, -item.getQty(), "OUT", invoice.getInvoiceNo(), invoice.getCreatedBy());
        }
        invoice.setTotalAmount(total);
        invoice.setStatus("PAID"); // simple: mark as paid for demo
        return invoiceRepo.save(invoice);
    }
}