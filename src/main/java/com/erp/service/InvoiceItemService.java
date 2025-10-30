package com.erp.service;

import com.erp.dto.InvoiceItemDTO;
import java.util.List;

public interface InvoiceItemService {
    List<InvoiceItemDTO> getItemsByInvoice(Long invoiceId);
    InvoiceItemDTO addItem(InvoiceItemDTO request);
    void deleteItem(Long id);
}
