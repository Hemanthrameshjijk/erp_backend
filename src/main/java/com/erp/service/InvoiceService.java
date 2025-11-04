package com.erp.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.erp.dto.InvoiceItemDTO;
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

    public Invoice createInvoice(InvoiceRequest request) {

        Customer customer = customerRepo.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Invoice invoice = new Invoice();
        invoice.setCustomer(customer);
        invoice.setStatus("DRAFT");

        BigDecimal total = BigDecimal.ZERO;

        for (InvoiceItemDTO itemReq : request.getItems()) {
            Product product = productRepo.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            InvoiceItem item = new InvoiceItem();
            item.setProduct(product);
            item.setQty(itemReq.getQty());
            item.setUnitPrice(itemReq.getUnitPrice());
            item.setInvoice(invoice);

            BigDecimal subtotal = itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQty()));
            item.setSubtotal(subtotal);

            total = total.add(subtotal);

            invoice.getItems().add(item);
        }

        invoice.setTotalAmount(total);
        invoice.setPaidAmount(BigDecimal.ZERO);
        invoice.setStatus("DRAFT");

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