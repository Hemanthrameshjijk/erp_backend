package com.erp.service;

import com.erp.dto.InvoiceItemDTO;
import entity.Invoice;
import entity.InvoiceItem;
import entity.Product;
import com.erp.repository.InvoiceItemRepository;
import com.erp.repository.InvoiceRepository;
import com.erp.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceItemServiceImpl implements InvoiceItemService {

    private final InvoiceItemRepository invoiceItemRepo;
    private final InvoiceRepository invoiceRepo;
    private final ProductRepository productRepo;

    @Override
    public List<InvoiceItemDTO> getItemsByInvoice(Long invoiceId) {
        return invoiceItemRepo.findByInvoiceId(invoiceId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceItemDTO addItem(InvoiceItemDTO request) {
        Invoice invoice = invoiceRepo.findById(request.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        InvoiceItem item = new InvoiceItem();
        item.setInvoice(invoice);
        item.setProduct(product);
        item.setQty(request.getQty());
        item.setUnitPrice(request.getUnitPrice());

        BigDecimal subtotal = request.getUnitPrice()
                .multiply(BigDecimal.valueOf(request.getQty()));

        item.setSubtotal(subtotal);

        InvoiceItem saved = invoiceItemRepo.save(item);
        return convertToDTO(saved);
    }

    @Override
    public void deleteItem(Long id) {
        invoiceItemRepo.deleteById(id);
    }

    private InvoiceItemDTO convertToDTO(InvoiceItem item) {
        InvoiceItemDTO dto = new InvoiceItemDTO();
        dto.setId(item.getId());
        dto.setInvoiceId(item.getInvoice().getId());
        dto.setProductId(item.getProduct().getId());
        dto.setQty(item.getQty());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }
}
