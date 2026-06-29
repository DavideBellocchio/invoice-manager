package com.davide.invoice_manager.service;

import com.davide.invoice_manager.domain.Invoice;
import com.davide.invoice_manager.domain.InvoiceItem;
import com.davide.invoice_manager.exception.ResourceNotFoundException;
import com.davide.invoice_manager.repository.InvoiceItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceItemService {

    private final InvoiceItemRepository invoiceItemRepository;
    private final InvoiceService invoiceService;

    public List<InvoiceItem> findAllByInvoice(Invoice invoice) {
        return invoiceItemRepository.findByInvoice(invoice);
    }
    public List<InvoiceItem> findAll(){
        return invoiceItemRepository.findAll();
    }
    public InvoiceItem findById(Long id) {
        return invoiceItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InvoiceItem not found with id: " + id ));
    }
    public InvoiceItem addInvoiceItem(InvoiceItem invoiceItem) {
        invoiceService.findById(invoiceItem.getInvoice().getId());
        invoiceService.validateIsDraft(invoiceItem.getInvoice());
        return invoiceItemRepository.save(invoiceItem);
    }
    public InvoiceItem updateInvoiceItem(InvoiceItem invoiceItem) {
        InvoiceItem item = findById(invoiceItem.getId());
        invoiceService.findById(item.getInvoice().getId());
        invoiceService.validateIsDraft(item.getInvoice());
        if(invoiceItem.getQuantity() != null){
            item.setQuantity(invoiceItem.getQuantity());
        }
        if(invoiceItem.getPrice() != null){
            item.setPrice(invoiceItem.getPrice());
        }
        if(invoiceItem.getProduct() != null){
            item.setProduct(invoiceItem.getProduct());
        }
        return invoiceItemRepository.save(item);
    }
    public void deleteInvoiceItem(Long id) {
        InvoiceItem item = findById(id);
        invoiceService.validateIsDraft(item.getInvoice());
        invoiceItemRepository.delete(item);
    }
}
