package com.davide.invoice_manager.controller;


import com.davide.invoice_manager.command.CreateBusinessProfileCommand;
import com.davide.invoice_manager.domain.BusinessProfile;
import com.davide.invoice_manager.domain.PersonType;
import com.davide.invoice_manager.dto.request.CreateBusinessProfileRequest;
import com.davide.invoice_manager.dto.response.BusinessProfileResponse;
import com.davide.invoice_manager.exception.ResourceNotFoundException;
import com.davide.invoice_manager.mapper.BusinessProfileMapper;
import com.davide.invoice_manager.service.BusinessProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
