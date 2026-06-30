package com.davide.invoice_manager.command;

public record CreateInvoiceItemCommand(
        Long invoiceId,
        Long productId,
        Integer quantity
) {
}
