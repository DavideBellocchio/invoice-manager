package com.davide.invoice_manager.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateBusinessProfileRequest(
        @Size(min = 3)
        String businessName,
        @Pattern(
                regexp = "^[0-9]{11}$",
                message = "Invalid vatCode format"
        )
        String vatCode,
        @Email
        String pec,
        @Pattern(
                regexp = "^\\d{9,11}$",
                message = "phoneNumber must be 9 to 11 digits"
        )
        String phoneNumber
) {
}
