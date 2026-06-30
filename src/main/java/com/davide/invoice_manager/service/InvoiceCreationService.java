package com.davide.invoice_manager.service;

import com.davide.invoice_manager.command.CreateInvoiceCommand;
import com.davide.invoice_manager.command.CreateInvoiceItemCommand;
import com.davide.invoice_manager.command.CreateInvoiceWithItemsCommand;
import com.davide.invoice_manager.command.InvoiceItemRequest;
import com.davide.invoice_manager.domain.Invoice;
import com.davide.invoice_manager.domain.InvoiceItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InvoiceCreationService {

    private final InvoiceService invoiceService;
    private final InvoiceItemService invoiceItemService;

    @Transactional
    public Invoice createInvoiceWithItems(CreateInvoiceWithItemsCommand command) {
        Invoice invoice = invoiceService.createInvoice(
                new CreateInvoiceCommand(
                        command.senderId(),
                        command.recipientId(),
                        command.issueDate(),
                        command.dueDate()
                )
        );
        for(InvoiceItemRequest it : command.items()){
            invoiceItemService.addInvoiceItem(
                    new CreateInvoiceItemCommand(
                            invoice.getId(),
                            it.productId(),
                            it.quantity()
                    )
            );
        }
        invoice.setItems(invoiceItemService.findAllByInvoice(invoice));
        return invoice;
    }
}
