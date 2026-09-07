package com.davide.invoice_manager.dto.request;

import com.davide.invoice_manager.domain.PersonType;
import jakarta.validation.constraints.*;

public record CreateBusinessProfileRequest(
        @NotBlank
        @Size(min = 3)
        String businessName,
        @NotBlank
        @Pattern(
                regexp = "^(?:[0-9]{11}|[A-Za-z0-9]{16})$",
                message = "fiscalCode must be 11 or 16 characters long."
        )
        String fiscalCode,
        @Pattern(
                regexp = "^[0-9]{11}$",
                message = "Invalid vatCode format"
        )
        String vatCode,
        @NotNull
        PersonType personType,
        @Email
        String pec,
        @Pattern(
                regexp = "^\\d{9,11}$",
                message = "phoneNumber must be 9 to 11 digits"
        )
        String phoneNumber
) {
    @AssertTrue(message = "vatCode is required for GIURIDICA")
    public boolean isValidVatCode() {
        if(personType == null || personType == PersonType.FISICA){
            return true;
        }
        return !(vatCode == null || vatCode.isBlank());
    }

    @AssertTrue(message = "fiscalCode must be 16 chars for FISICA, 11 for GIURIDICA")
    public boolean isFiscalCodeConsistentWithType() {
        if(personType == null || fiscalCode == null){
            return true;
        }
        if(personType == PersonType.FISICA && fiscalCode.length() != 16){
            return false;
        }
        if(personType == PersonType.GIURIDICA && fiscalCode.length() != 11){
            return false;
        }
        return true;
    }
}
