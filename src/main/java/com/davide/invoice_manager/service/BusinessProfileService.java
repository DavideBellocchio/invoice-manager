package com.davide.invoice_manager.service;

import com.davide.invoice_manager.command.CreateBusinessProfileCommand;
import com.davide.invoice_manager.command.UpdateBusinessProfileCommand;
import com.davide.invoice_manager.domain.BusinessProfile;
import com.davide.invoice_manager.domain.User;
import com.davide.invoice_manager.exception.ResourceNotFoundException;
import com.davide.invoice_manager.repository.BusinessProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessProfileService {

    private final BusinessProfileRepository businessProfileRepository;
    private final UserService userService;

    public List<BusinessProfile> findAll() {
        return businessProfileRepository.findAll();
    }

    public BusinessProfile getBusinessProfileByUser(User user) {
        return businessProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("BusinessProfile not found for user: " + user.getUsername()));
    }

    public BusinessProfile getBusinessProfileById(Long id) {
        return businessProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BusinessProfile not found for id: " + id));
    }

    public BusinessProfile createBusinessProfile(CreateBusinessProfileCommand command) {
        User user = null;
        if(command.userId() != null){
            user = userService.getUserById(command.userId());
        }
        BusinessProfile bp = new BusinessProfile();
        bp.setBusinessName(command.businessName());
        bp.setFiscalCode(command.fiscalCode());
        bp.setVatCode(command.vatCode());
        bp.setPersonType(command.personType());
        bp.setPec(command.pec());
        bp.setPhoneNumber(command.phoneNumber());
        bp.setUser(user);
        return businessProfileRepository.save(bp);
    }

    public BusinessProfile updateBusinessProfile(Long businessProfileId, UpdateBusinessProfileCommand command) {
        BusinessProfile existingBusinessProfile = getBusinessProfileById(businessProfileId);

        if(command.businessName() != null) {
            existingBusinessProfile.setBusinessName(command.businessName());
        }

        if(command.fiscalCode() != null) {
            existingBusinessProfile.setFiscalCode(command.fiscalCode());
        }

        if(command.vatCode() != null) {
            existingBusinessProfile.setVatCode(command.vatCode());
        }

        if(command.personType() != null) {
            existingBusinessProfile.setPersonType(command.personType());
        }

        if(command.pec() != null) {
            existingBusinessProfile.setPec(command.pec());
        }

        if(command.phoneNumber() != null) {
            existingBusinessProfile.setPhoneNumber(command.phoneNumber());
        }

        return businessProfileRepository.save(existingBusinessProfile);
    }

    public void deleteBusinessProfile(Long id) {
        BusinessProfile bp = businessProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BusinessProfile not found for id: " + id));
        businessProfileRepository.delete(bp);
    }
}
