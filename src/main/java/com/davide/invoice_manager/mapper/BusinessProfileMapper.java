package com.davide.invoice_manager.mapper;


import com.davide.invoice_manager.command.CreateBusinessProfileCommand;
import com.davide.invoice_manager.command.UpdateBusinessProfileCommand;
import com.davide.invoice_manager.domain.BusinessProfile;
import com.davide.invoice_manager.dto.request.CreateBusinessProfileRequest;
import com.davide.invoice_manager.dto.request.UpdateBusinessProfileRequest;
import com.davide.invoice_manager.dto.response.BusinessProfileResponse;
import org.springframework.stereotype.Component;

@Component
public class BusinessProfileMapper {

    public CreateBusinessProfileCommand toCommand(CreateBusinessProfileRequest businessProfile) {
        return new CreateBusinessProfileCommand(
                businessProfile.businessName(),
                businessProfile.fiscalCode(),
                businessProfile.vatCode(),
                businessProfile.personType(),
                businessProfile.pec(),
                businessProfile.phoneNumber(),
                null // TODO: da SecurityContext
        );
    }

    public UpdateBusinessProfileCommand toCommand(UpdateBusinessProfileRequest businessProfile) {
        return new UpdateBusinessProfileCommand(
                businessProfile.businessName(),
                businessProfile.vatCode(),
                businessProfile.pec(),
                businessProfile.phoneNumber()
        );
    }

    public BusinessProfileResponse toResponse(BusinessProfile businessProfile) {
        Long userId = businessProfile.getUser() == null ? null : businessProfile.getUser().getId();
        return new BusinessProfileResponse(
                businessProfile.getId(),
                businessProfile.getBusinessName(),
                businessProfile.getFiscalCode(),
                businessProfile.getVatCode(),
                businessProfile.getPersonType(),
                businessProfile.getPec(),
                businessProfile.getPhoneNumber(),
                userId
        );
    }
}
