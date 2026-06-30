package com.davide.invoice_manager.service;

import com.davide.invoice_manager.command.CreateInvoiceItemCommand;
import com.davide.invoice_manager.command.UpdateInvoiceItemCommand;
import com.davide.invoice_manager.domain.Invoice;
import com.davide.invoice_manager.domain.InvoiceItem;
import com.davide.invoice_manager.domain.Product;
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
    private final ProductService productService;

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
    public InvoiceItem addInvoiceItem(CreateInvoiceItemCommand command) {
        Invoice invoice = invoiceService.findById(command.invoiceId());
        invoiceService.validateIsDraft(invoice);
        Product product = productService.findById(command.productId());
        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setInvoice(invoice);
        invoiceItem.setProduct(product);
        invoiceItem.setQuantity(command.quantity());
        invoiceItem.setPrice(product.getPrice());
        return invoiceItemRepository.save(invoiceItem);
    }
    public InvoiceItem updateInvoiceItem(Long invoiceItemId, UpdateInvoiceItemCommand command) {
        InvoiceItem item = findById(invoiceItemId);
        invoiceService.validateIsDraft(item.getInvoice());
        if(command.quantity() != null){
            item.setQuantity(command.quantity());
        }
        return invoiceItemRepository.save(item);
    }
    public void deleteInvoiceItem(Long id) {
        InvoiceItem item = findById(id);
        invoiceService.validateIsDraft(item.getInvoice());
        invoiceItemRepository.delete(item);
    }
}
