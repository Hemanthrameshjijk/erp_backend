package com.erp.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.dto.InvoiceRequest;
import com.erp.dto.PaymentRequest;
import com.erp.repository.InvoiceItemRepository;
import com.erp.repository.InvoiceRepository;
import com.erp.repository.ProductRepository;

import entity.Customer;
import entity.Invoice;
import entity.InvoiceItem;
import entity.Product;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepo;
    private final ProductRepository productRepo;
    private final ProductService productService;
    @Autowired private com.erp.repository.CustomerRepository customerRepo;
    @Autowired private InvoiceItemRepository itemRepo;


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
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(invoiceRepo.findAll());}
    
    public ResponseEntity<?> getById(Long id) {

        var invoice = invoiceRepo.findById(id).orElse(null);

        if (invoice == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Invoice not found"));
        }

        return ResponseEntity.ok(invoice);
    }

    public ResponseEntity<?> createInvoice(InvoiceRequest req) {
        Optional<Customer> c = customerRepo.findById(req.getCustomerId());
        if (c.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "Invalid customer"));

        Invoice invoice = new Invoice();
        invoice.setCustomer(c.get());
        invoice.setStatus(req.getStatus() != null ? req.getStatus() : "DRAFT");
        invoice.setPaidAmount(BigDecimal.ZERO);

        // Save temporary to get Invoice ID
        invoice = invoiceRepo.save(invoice);

        BigDecimal total = BigDecimal.ZERO;

        for (var itemReq : req.getItems()) {

            Product p = productRepo.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setProduct(p);
            item.setQty(itemReq.getQty());
            item.setUnitPrice(itemReq.getUnitPrice());
            item.setSubtotal(itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQty())));

            total = total.add(item.getSubtotal());
            itemRepo.save(item);
        }

        invoice.setTotalAmount(total);
        invoiceRepo.save(invoice);

        return ResponseEntity.ok(invoice);
    }
    public ResponseEntity<?> updateInvoice(Long id, InvoiceRequest req) {
        Invoice invoice = invoiceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (req.getStatus() != null) invoice.setStatus(req.getStatus());
        invoiceRepo.save(invoice);

        return ResponseEntity.ok(invoice);
    }
    public ResponseEntity<?> delete(Long id) {
        if (!invoiceRepo.existsById(id))
            return ResponseEntity.status(404).body(Map.of("error", "Invoice not found"));

        invoiceRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Invoice deleted"));
    }
    public ResponseEntity<?> findByCustomer(Long customerId) {
        return ResponseEntity.ok(invoiceRepo.findByCustomerId(customerId));
    }
    public ResponseEntity<?> findByStatus(String status) {
        return ResponseEntity.ok(invoiceRepo.findByStatus(status));
    }
    public ResponseEntity<?> recordPayment(Long id, PaymentRequest req) {
        Invoice invoice = invoiceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setPaidAmount(invoice.getPaidAmount().add(req.getAmountPaid()));

        if (invoice.getPaidAmount().compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus("PAID");
        } else {
            invoice.setStatus("PARTIAL");
        }

        invoiceRepo.save(invoice);
        return ResponseEntity.ok(invoice);
    }
    public Object getSummary() {
        Object result = invoiceRepo.getSummary();
        Object[] row = (Object[]) result;

        return Map.of(
                "totalSales", row[0],
                "unpaidAmount", row[1]
        );
    }
}