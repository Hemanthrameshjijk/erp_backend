package com.erp.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.dto.InvoiceItemDTO;
import com.erp.dto.InvoiceItemResponseDTO;
import com.erp.dto.InvoiceRequest;
import com.erp.dto.InvoiceResponseDTO;
import com.erp.dto.InvoiceSummaryDTO;
import com.erp.dto.PaymentRequest;
import com.erp.repository.InvoiceRepository;
import com.erp.repository.ProductRepository;
import com.erp.security.CustomUserDetails;

import entity.Customer;
import entity.Invoice;
import entity.InvoiceItem;
import entity.Product;
import entity.User;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepo;
    private final ProductRepository productRepo;
    private final ProductService productService;
    @Autowired private com.erp.repository.CustomerRepository customerRepo;


    public InvoiceService(InvoiceRepository invoiceRepo, ProductRepository productRepo, ProductService productService) {
        this.invoiceRepo = invoiceRepo;
        this.productRepo = productRepo;
        this.productService = productService;
    }

    public Invoice createInvoice(InvoiceRequest request) {
            // 1. Get authenticated user from SecurityContext
            // The JwtFilter sets the authentication with CustomUserDetails
            org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getPrincipal() == null) {
                throw new RuntimeException("User not authenticated");
            }
            
            // Cast to CustomUserDetails to access User entity
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User createdBy = userDetails.getUser();
            
            // 2. Get customer
            Customer customer = customerRepo.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
            
            // 3. Generate invoice number (format: INV-YYYYMMDD-HHmmss)
            String invoiceNo = generateInvoiceNumber();
            
            // 4. Create invoice entity
            Invoice invoice = new Invoice();
            invoice.setInvoiceNo(invoiceNo);
            invoice.setCustomer(customer);
            invoice.setCreatedBy(createdBy);
            invoice.setCreatedAt(Instant.now());
            invoice.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
            invoice.setPaidAmount(BigDecimal.ZERO);
            
            // 5. Calculate total amount and create invoice items
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<InvoiceItem> items = new ArrayList<>();
            
            for (InvoiceItemDTO itemDTO : request.getItems()) {
                Product product = productRepo.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemDTO.getProductId()));
                
                InvoiceItem item = new InvoiceItem();
                item.setInvoice(invoice);
                item.setProduct(product);
                item.setQty(itemDTO.getQty());
                item.setUnitPrice(itemDTO.getUnitPrice());
                
                // Calculate subtotal
                BigDecimal subtotal = itemDTO.getUnitPrice()
                    .multiply(BigDecimal.valueOf(itemDTO.getQty()));
                item.setSubtotal(subtotal);
                
                totalAmount = totalAmount.add(subtotal);
                items.add(item);
            }
            
            invoice.setTotalAmount(totalAmount);
            invoice.setItems(items);
            
            // 6. Save invoice (cascade will save items)
            return invoiceRepo.save(invoice);
        }
        
        private String generateInvoiceNumber() {
            // Option 1: Using timestamp (recommended for simplicity)
            LocalDateTime now = LocalDateTime.now();
            String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            return "INV-" + timestamp;
            
            // Option 2: Using sequence/counter (requires a sequence table for uniqueness)
            // Long nextSeq = getNextInvoiceSequence();
            // String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            // return "INV-" + datePrefix + "-" + String.format("%05d", nextSeq);
            
            // Option 3: Using UUID (shorter, but less readable)
            // return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        
        // ✅ Or return DTOs to avoid circular reference
        public List<InvoiceResponseDTO> findAllDTOs() {
            return invoiceRepo.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        }
        
        private InvoiceResponseDTO convertToDTO(Invoice invoice) {
            InvoiceResponseDTO dto = new InvoiceResponseDTO();
            dto.setId(invoice.getId());
            dto.setInvoiceNo(invoice.getInvoiceNo());
            dto.setTotalAmount(invoice.getTotalAmount());
            dto.setPaidAmount(invoice.getPaidAmount());
            dto.setStatus(invoice.getStatus());
            dto.setCreatedAt(invoice.getCreatedAt());

            // Customer
            if (invoice.getCustomer() != null) {
                dto.setCustomerId(invoice.getCustomer().getId());
                dto.setCustomerName(invoice.getCustomer().getName());
            }

            // Created by user
            if (invoice.getCreatedBy() != null) {
                dto.setCreatedByUsername(invoice.getCreatedBy().getUsername());
            }

            // Items
            List<InvoiceItemResponseDTO> itemDTOs = invoice.getItems().stream()
                    .map(item -> {
                        InvoiceItemResponseDTO itemDTO = new InvoiceItemResponseDTO();
                        itemDTO.setId(item.getId());
                        
                        if (item.getProduct() != null) {
                            itemDTO.setProductId(item.getProduct().getId());
                            itemDTO.setProductName(item.getProduct().getName());
                            itemDTO.setProductSku(item.getProduct().getSku());
                        }
                        
                        itemDTO.setQty(item.getQty());
                        itemDTO.setUnitPrice(item.getUnitPrice());
                        itemDTO.setSubtotal(item.getSubtotal());
                        return itemDTO;
                    })
                    .collect(Collectors.toList());

            dto.setItems(itemDTOs);

            return dto;
        }

        // ✅ Find all invoices
        @Transactional
        public List<InvoiceResponseDTO> findAll() {
            return invoiceRepo.findAllWithDetails().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        }

        
        // ✅ Find by ID
        @Transactional
        public InvoiceResponseDTO getById(Long id) {
            return invoiceRepo.findById(id)
                    .map(this::convertToDTO)
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));
        }

        public InvoiceResponseDTO updateInvoice(Long id, InvoiceRequest request) {

            Invoice invoice = invoiceRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));

            Customer customer = customerRepo.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            invoice.setCustomer(customer);
            invoice.getItems().clear();

            BigDecimal total = BigDecimal.ZERO;

            for (InvoiceItemDTO itemDTO : request.getItems()) {
                Product product = productRepo.findById(itemDTO.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                InvoiceItem item = new InvoiceItem();
                item.setProduct(product);
                item.setQty(itemDTO.getQty());
                item.setUnitPrice(product.getPrice());
                item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQty())));
                item.setInvoice(invoice);
                
                invoice.getItems().add(item);
                total = total.add(item.getSubtotal());
            }

            invoice.setTotalAmount(total);

            if (request.getStatus() != null) {
                invoice.setStatus(request.getStatus());
            }

            invoiceRepo.save(invoice);
            return convertToDTO(invoice);
        }

        // ✅ Update payment only
        @Transactional
        public InvoiceResponseDTO updatePayment(Long id, PaymentRequest request) {
            Invoice invoice = invoiceRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));

            BigDecimal paid = invoice.getPaidAmount().add(request.getAmountPaid());
            invoice.setPaidAmount(paid);

            if (paid.compareTo(invoice.getTotalAmount()) >= 0) {
                invoice.setStatus("PAID");
            } else if (paid.compareTo(BigDecimal.ZERO) > 0) {
                invoice.setStatus("PARTIAL");
            }

            invoiceRepo.save(invoice);
            return convertToDTO(invoice);
        }
        @Transactional
        public List<InvoiceResponseDTO> findByStatus(String status) {
            return invoiceRepo.findByStatus(status).stream()
                .map(this::convertToDTO)
                .toList();
        }
        
        public InvoiceSummaryDTO getSummary() {
            List<Invoice> invoices = invoiceRepo.findAll();

            BigDecimal totalSales = invoices.stream()
                .map(Invoice::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalPaid = invoices.stream()
                .map(Invoice::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalUnpaid = totalSales.subtract(totalPaid);

            long unpaidInvoices = invoices.stream()
                .filter(inv -> inv.getTotalAmount().compareTo(inv.getPaidAmount()) > 0)
                .count();

            InvoiceSummaryDTO summary = new InvoiceSummaryDTO();
            summary.setTotalSales(totalSales);
            summary.setTotalPaid(totalPaid);
            summary.setTotalUnpaid(totalUnpaid);
            summary.setTotalInvoices((long) invoices.size());
            summary.setUnpaidInvoices(unpaidInvoices);

            return summary;
        }

        public ResponseEntity<?> delete(Long id) {
            invoiceRepo.deleteById(id);
            return ResponseEntity.ok().build();
        }
        
        public List<InvoiceResponseDTO> findByCustomer(Long customerId) {
            List<Invoice> invoices = invoiceRepo.findByCustomerWithItems(customerId);
            return invoices.stream().map(this::convertToDTO).collect(Collectors.toList());
        }

        public InvoiceResponseDTO convertToDTO1(Invoice invoice) {
            InvoiceResponseDTO dto = new InvoiceResponseDTO();
            dto.setId(invoice.getId());
            dto.setInvoiceNo(invoice.getInvoiceNo());
            dto.setCustomerId(invoice.getCustomer().getId());
            dto.setCustomerName(invoice.getCustomer().getName());
            dto.setTotalAmount(invoice.getTotalAmount());
            dto.setPaidAmount(invoice.getPaidAmount());
            dto.setStatus(invoice.getStatus());
            dto.setCreatedAt(invoice.getCreatedAt());
            dto.setCreatedByUsername(
                invoice.getCreatedBy() != null ? invoice.getCreatedBy().getUsername() : null
            );

            dto.setItems(
                invoice.getItems().stream().map(item -> {
                    InvoiceItemResponseDTO itemDTO = new InvoiceItemResponseDTO();
                    itemDTO.setId(item.getId());
                    itemDTO.setProductId(item.getProduct().getId());
                    itemDTO.setProductName(item.getProduct().getName());
                    itemDTO.setProductSku(item.getProduct().getSku());
                    itemDTO.setQty(item.getQty());
                    itemDTO.setUnitPrice(item.getUnitPrice());
                    itemDTO.setSubtotal(item.getSubtotal());
                    return itemDTO;
                }).toList()
            );

            return dto;
        }
        @Transactional(readOnly = true)
        public List<InvoiceResponseDTO> getRecentInvoices() {
            return invoiceRepo.findTop10ByOrderByCreatedAtDesc()
                    .stream()
                    .map(this::convertToDTO)
                    .toList();
        }
    }