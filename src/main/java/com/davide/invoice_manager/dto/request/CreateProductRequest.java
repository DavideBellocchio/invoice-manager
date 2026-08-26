package com.davide.invoice_manager.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank @Size(min = 3) String name,
        String description,
        @NotNull @Positive BigDecimal price
) {
}
