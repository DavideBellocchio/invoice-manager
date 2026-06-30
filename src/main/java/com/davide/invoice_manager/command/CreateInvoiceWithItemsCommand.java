package com.davide.invoice_manager.command;

import java.time.LocalDate;
import java.util.List;

public record CreateInvoiceWithItemsCommand(
        Long senderId,
        Long recipientId,
        LocalDate issueDate,
        LocalDate dueDate,
        List<InvoiceItemRequest> items
) {
}
