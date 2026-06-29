package com.davide.invoice_manager.service;

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

    public BusinessProfile createBusinessProfile(BusinessProfile businessProfile) {
        return businessProfileRepository.save(businessProfile);
    }

    public BusinessProfile updateBusinessProfile(BusinessProfile businessProfile) {
        BusinessProfile existingBusinessProfile = businessProfileRepository.findById(businessProfile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("BusinessProfile not found for id: " + businessProfile.getId()));

        if(businessProfile.getBusinessName() != null) {
            existingBusinessProfile.setBusinessName(businessProfile.getBusinessName());
        }

        if(businessProfile.getFiscalCode() != null) {
            existingBusinessProfile.setFiscalCode(businessProfile.getFiscalCode());
        }

        if(businessProfile.getVatCode() != null) {
            existingBusinessProfile.setVatCode(businessProfile.getVatCode());
        }

        if(businessProfile.getPersonType() != null) {
            existingBusinessProfile.setPersonType(businessProfile.getPersonType());
        }

        if(businessProfile.getPec() != null) {
            existingBusinessProfile.setPec(businessProfile.getPec());
        }

        if(businessProfile.getPhoneNumber() != null) {
            existingBusinessProfile.setPhoneNumber(businessProfile.getPhoneNumber());
        }

        if(businessProfile.getUser() != null) {
            existingBusinessProfile.setUser(businessProfile.getUser());
        }

        return businessProfileRepository.save(existingBusinessProfile);
    }

    public void deleteBusinessProfile(Long id) {
        BusinessProfile bp = businessProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BusinessProfile not found for id: " + id));
        businessProfileRepository.delete(bp);
    }
}
