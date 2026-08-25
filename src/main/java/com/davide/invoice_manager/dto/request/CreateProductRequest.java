package com.davide.invoice_manager.dto.request;

import java.math.BigDecimal;

public record CreateProductRequest(
        String name,
        String description,
        BigDecimal price

) {
}
