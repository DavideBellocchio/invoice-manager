package com.davide.invoice_manager.command;

import java.time.LocalDate;

public record CreateInvoiceCommand(
        Long senderId,
        Long recipientId,
        LocalDate issueDate,
        LocalDate dueDate
) {
}
