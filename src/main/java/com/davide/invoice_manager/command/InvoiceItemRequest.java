package com.davide.invoice_manager.command;

public record InvoiceItemRequest(
        Long productId,
        Integer quantity
) {
}
