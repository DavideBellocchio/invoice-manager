package com.davide.invoice_manager.command;

import com.davide.invoice_manager.domain.PersonType;

public record CreateBusinessProfileCommand(
        String businessName,
        String fiscalCode,
        String vatCode,
        PersonType personType,
        String pec,
        String phoneNumber,
        Long userId
) {
}
