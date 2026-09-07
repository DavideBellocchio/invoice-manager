package com.davide.invoice_manager.controller;

import com.davide.invoice_manager.command.CreateBusinessProfileCommand;
import com.davide.invoice_manager.command.UpdateBusinessProfileCommand;
import com.davide.invoice_manager.dto.request.CreateBusinessProfileRequest;
import com.davide.invoice_manager.dto.request.UpdateBusinessProfileRequest;
import com.davide.invoice_manager.dto.response.BusinessProfileResponse;
import com.davide.invoice_manager.mapper.BusinessProfileMapper;
import com.davide.invoice_manager.service.BusinessProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business-profiles")
@RequiredArgsConstructor
public class BusinessProfileController {

    private final BusinessProfileService businessProfileService;
    private final BusinessProfileMapper businessProfileMapper;

    @GetMapping
    public ResponseEntity<List<BusinessProfileResponse>> getAllBusinessProfiles() {
        List<BusinessProfileResponse> businessProfiles = businessProfileService.findAll().stream().map(businessProfileMapper::toResponse).toList();
        return ResponseEntity.ok(businessProfiles);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<BusinessProfileResponse> getBusinessProfileByUser(@PathVariable("userId") Long userId) {
        BusinessProfileResponse businessProfile = businessProfileMapper.toResponse(businessProfileService.getBusinessProfileByUser(userId));
        return ResponseEntity.ok(businessProfile);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessProfileResponse> getBusinessProfileById(@PathVariable Long id) {
        BusinessProfileResponse businessProfile = businessProfileMapper.toResponse(businessProfileService.getBusinessProfileById(id));
        return ResponseEntity.ok(businessProfile);
    }

    @PostMapping
    public ResponseEntity<BusinessProfileResponse> create(@Valid @RequestBody CreateBusinessProfileRequest createBusinessProfileRequest) {
        CreateBusinessProfileCommand command = businessProfileMapper.toCommand(createBusinessProfileRequest);
        BusinessProfileResponse businessProfile = businessProfileMapper.toResponse(businessProfileService.createBusinessProfile(command));
        return ResponseEntity.status(HttpStatus.CREATED).body(businessProfile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessProfileResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateBusinessProfileRequest  updateBusinessProfileRequest) {
        UpdateBusinessProfileCommand command = businessProfileMapper.toCommand(updateBusinessProfileRequest);
        BusinessProfileResponse businessProfile = businessProfileMapper.toResponse(businessProfileService.updateBusinessProfile(id, command));
        return ResponseEntity.ok(businessProfile);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        businessProfileService.deleteBusinessProfile(id);
        return ResponseEntity.noContent().build();
    }


}
