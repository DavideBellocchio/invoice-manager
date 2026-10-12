package com.davide.invoice_manager.dto.request;

import java.math.BigDecimal;

public record UpdateProductRequest(
        BigDecimal price
) {
}
