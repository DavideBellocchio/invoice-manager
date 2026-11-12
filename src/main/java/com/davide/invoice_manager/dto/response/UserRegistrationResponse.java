package com.davide.invoice_manager.dto.response;

import com.davide.invoice_manager.domain.Role;

import java.time.LocalDateTime;

public record UserRegistrationResponse(
        Long id,
        String username,
        Role role,
        LocalDateTime createdAt
) {
}
