package com.davide.invoice_manager.controller;


import com.davide.invoice_manager.command.CreateBusinessProfileCommand;
import com.davide.invoice_manager.command.UpdateBusinessProfileCommand;
import com.davide.invoice_manager.domain.BusinessProfile;
import com.davide.invoice_manager.domain.PersonType;
import com.davide.invoice_manager.dto.request.CreateBusinessProfileRequest;
import com.davide.invoice_manager.dto.request.UpdateBusinessProfileRequest;
import com.davide.invoice_manager.dto.response.BusinessProfileResponse;
import com.davide.invoice_manager.exception.ResourceNotFoundException;
import com.davide.invoice_manager.mapper.BusinessProfileMapper;
import com.davide.invoice_manager.service.BusinessProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BusinessProfileController.class)
public class BusinessProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BusinessProfileService businessProfileService;

    @MockitoBean
    private BusinessProfileMapper businessProfileMapper;

    private BusinessProfile businessProfile;
    private BusinessProfileResponse businessProfileResponse;

    @BeforeEach
    public void init(){
        businessProfile = new BusinessProfile();
        businessProfileResponse = new BusinessProfileResponse(
                1L,
                "ProvaAzienda",
                "BLLDVD96H11C816J",
                "",
                PersonType.FISICA,
                "",
                "",
                1L
        );
    }

    @Test
    public void shouldReturnAllBusinessProfiles() throws Exception {
        List<BusinessProfile> businessProfiles = List.of(businessProfile);
        when(businessProfileService.findAll()).thenReturn(businessProfiles);
        when(businessProfileMapper.toResponse(businessProfile)).thenReturn(businessProfileResponse);

        mockMvc.perform(get("/api/business-profiles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].businessName").value("ProvaAzienda"))
                .andExpect(jsonPath("$[0].fiscalCode").value("BLLDVD96H11C816J"))
                .andExpect(jsonPath("$[0].vatCode").value(""))
                .andExpect(jsonPath("$[0].personType").value(PersonType.FISICA.toString()))
                .andExpect(jsonPath("$[0].pec").value(""))
                .andExpect(jsonPath("$[0].phoneNumber").value(""))
                .andExpect(jsonPath("$[0].userId").value(1));

    }

    @Test
    public void shouldReturnBusinessProfile_whenIdExists() throws Exception {
        when(businessProfileService.getBusinessProfileById(1L)).thenReturn(businessProfile);
        when(businessProfileMapper.toResponse(businessProfile)).thenReturn(businessProfileResponse);
        mockMvc.perform(get("/api/business-profiles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.businessName").value("ProvaAzienda"))
                .andExpect(jsonPath("$.fiscalCode").value("BLLDVD96H11C816J"))
                .andExpect(jsonPath("$.vatCode").value(""))
                .andExpect(jsonPath("$.personType").value(PersonType.FISICA.toString()))
                .andExpect(jsonPath("$.pec").value(""))
                .andExpect(jsonPath("$.phoneNumber").value(""))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    public void shouldReturnNotFound_whenIdDoesNotExist() throws Exception {
        when(businessProfileService.getBusinessProfileById(1L)).thenThrow(new ResourceNotFoundException("Business Profile Not Found"));
        mockMvc.perform(get("/api/business-profiles/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnBusinessProfile_whenUserExists() throws Exception {
        when(businessProfileService.getBusinessProfileByUser(1L)).thenReturn(businessProfile);
        when(businessProfileMapper.toResponse(businessProfile)).thenReturn(businessProfileResponse);
        mockMvc.perform(get("/api/business-profiles/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.businessName").value("ProvaAzienda"))
                .andExpect(jsonPath("$.fiscalCode").value("BLLDVD96H11C816J"))
                .andExpect(jsonPath("$.vatCode").value(""))
                .andExpect(jsonPath("$.personType").value(PersonType.FISICA.toString()))
                .andExpect(jsonPath("$.pec").value(""))
                .andExpect(jsonPath("$.phoneNumber").value(""))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    public void shouldReturnNotFound_whenUserHasNoProfile() throws Exception {
        when(businessProfileService.getBusinessProfileByUser(1L)).thenThrow(new ResourceNotFoundException("Business Profile Not Found"));
        mockMvc.perform(get("/api/business-profiles/user/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldCreateBusinessProfile_whenRequestIsValid() throws Exception {
        CreateBusinessProfileCommand create = new CreateBusinessProfileCommand(
                "ProvaAzienda",
                "BLLDVD96H11C816J",
                "",
                PersonType.FISICA,
                "",
                "",
                1L
        );
        when(businessProfileMapper.toCommand(any(CreateBusinessProfileRequest.class))).thenReturn(create);
        when(businessProfileService.createBusinessProfile(any(CreateBusinessProfileCommand.class))).thenReturn(businessProfile);
        when(businessProfileMapper.toResponse(any(BusinessProfile.class))).thenReturn(businessProfileResponse);

        mockMvc.perform(post("/api/business-profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"businessName\": \"ProvaAzienda\", " +
                        "\"fiscalCode\": \"BLLDVD96H11C816J\", " +
                        "\"vatCode\": \"\", " +
                        "\"personType\": \"FISICA\"," +
                        "\"pec\": \"\"," +
                        "\"phoneNumber\": \"\"," +
                        "\"userId\":  1 " +
                        " }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.businessName").value("ProvaAzienda"))
                .andExpect(jsonPath("$.fiscalCode").value("BLLDVD96H11C816J"))
                .andExpect(jsonPath("$.vatCode").value(""))
                .andExpect(jsonPath("$.personType").value(PersonType.FISICA.toString()))
                .andExpect(jsonPath("$.pec").value(""))
                .andExpect(jsonPath("$.phoneNumber").value(""))
                .andExpect(jsonPath("$.userId").value(1));

    }

    @Test
    public void shouldReturnBadRequest_whenGiuridicaHasNoVatCode() throws Exception {

        mockMvc.perform(post("/api/business-profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"businessName\": \"ProvaAzienda\", " +
                                "\"fiscalCode\": \"BLLDVD96H11C816J\", " +
                                "\"vatCode\": \"\", " +
                                "\"personType\": \"GIURIDICA\"," +
                                "\"pec\": \"\"," +
                                "\"phoneNumber\": \"\"," +
                                "\"userId\":  1 " +
                                " }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.validVatCode").exists());
        verify(businessProfileService, never()).createBusinessProfile(any(CreateBusinessProfileCommand.class));

    }

    @Test
    public void shouldReturnBadRequest_whenFiscalCodeIsInvalid() throws Exception {

        mockMvc.perform(post("/api/business-profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"businessName\": \"ProvaAzienda\", " +
                                "\"fiscalCode\": \"BLLDVD96H11C816J\", " +
                                "\"vatCode\": \"11111111111\", " +
                                "\"personType\": \"GIURIDICA\"," +
                                "\"pec\": \"\"," +
                                "\"phoneNumber\": \"\"," +
                                "\"userId\":  1 " +
                                " }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.fiscalCodeConsistentWithType").exists());
        verify(businessProfileService, never()).createBusinessProfile(any(CreateBusinessProfileCommand.class));

    }

    @Test
    public void shouldUpdateBusinessProfile_whenRequestIsValid() throws Exception {
        UpdateBusinessProfileCommand update = new UpdateBusinessProfileCommand("ProvaUpdate","22222222222", "", "");
        businessProfileResponse = new BusinessProfileResponse(1L, "ProvaUpdate", "11111111111", "22222222222", PersonType.GIURIDICA, "","",1L);
        when(businessProfileMapper.toCommand(any(UpdateBusinessProfileRequest.class))).thenReturn(update);
        when(businessProfileService.updateBusinessProfile(1L, update)).thenReturn(businessProfile);
        when(businessProfileMapper.toResponse(any(BusinessProfile.class))).thenReturn(businessProfileResponse);

        mockMvc.perform(put("/api/business-profiles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"businessName\": \"ProvaUpdate\", " +
                        "\"fiscalCode\": \"11111111111\", " +
                        "\"vatCode\": \"22222222222\", " +
                        "\"personType\": \"GIURIDICA\"," +
                        "\"pec\": \"\"," +
                        "\"phoneNumber\": \"\"," +
                        "\"userId\":  1 " +
                        " }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.businessName").value("ProvaUpdate"))
                .andExpect(jsonPath("$.fiscalCode").value("11111111111"))
                .andExpect(jsonPath("$.vatCode").value("22222222222"))
                .andExpect(jsonPath("$.personType").value(PersonType.GIURIDICA.toString()))
                .andExpect(jsonPath("$.pec").value(""))
                .andExpect(jsonPath("$.phoneNumber").value(""))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    public void shouldReturnNotFound_whenUpdatingMissingId() throws Exception {
        UpdateBusinessProfileCommand update = new UpdateBusinessProfileCommand("ProvaUpdate","22222222222", "", "");
        when(businessProfileMapper.toCommand(any(UpdateBusinessProfileRequest.class))).thenReturn(update);
        when(businessProfileService.updateBusinessProfile(1L, update)).thenThrow(new ResourceNotFoundException("Business Profile not found with id: " + 1L));

        mockMvc.perform(put("/api/business-profiles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"businessName\": \"ProvaUpdate\", " +
                        "\"fiscalCode\": \"11111111111\", " +
                        "\"vatCode\": \"22222222222\", " +
                        "\"personType\": \"GIURIDICA\"," +
                        "\"pec\": \"\"," +
                        "\"phoneNumber\": \"\"," +
                        "\"userId\":  1 " +
                        " }"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldDeleteBusinessProfile_whenIdExists() throws Exception {
        mockMvc.perform(delete("/api/business-profiles/1"))
                .andExpect(status().isNoContent());
        Mockito.verify(businessProfileService, Mockito.times(1)).deleteBusinessProfile(1L);
    }

    @Test
    public void shouldReturnNotFound_whenDeletingMissingId() throws Exception {
        doThrow(new ResourceNotFoundException("Business Profile not found with id: " + 1L))
                .when(businessProfileService).deleteBusinessProfile(1L);
        mockMvc.perform(delete("/api/business-profiles/1"))
                .andExpect(status().isNotFound());
    }
}
