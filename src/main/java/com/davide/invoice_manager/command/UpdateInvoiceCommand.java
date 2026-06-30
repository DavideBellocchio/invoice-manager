package com.davide.invoice_manager.command;

import java.time.LocalDate;

public record UpdateInvoiceCommand(
        Long recipientId,
        LocalDate issueDate,
        LocalDate dueDate
) {
}
