package com.davide.invoice_manager.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(
        @NotBlank
        @Email
        String username,
        @NotBlank
        @Size(min = 12, max = 64)
        String password
) {
}
