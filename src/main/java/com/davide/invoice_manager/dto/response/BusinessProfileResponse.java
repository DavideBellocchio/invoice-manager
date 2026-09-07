package com.davide.invoice_manager.dto.response;

import com.davide.invoice_manager.domain.PersonType;

public record BusinessProfileResponse(
        Long id,
        String businessName,
        String fiscalCode,
        String vatCode,
        PersonType personType,
        String pec,
        String phoneNumber,
        Long userId
) {
}
