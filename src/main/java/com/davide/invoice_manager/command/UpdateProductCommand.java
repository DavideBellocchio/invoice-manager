package com.davide.invoice_manager.command;

import java.math.BigDecimal;

public record UpdateProductCommand(
        BigDecimal price
) {
}
