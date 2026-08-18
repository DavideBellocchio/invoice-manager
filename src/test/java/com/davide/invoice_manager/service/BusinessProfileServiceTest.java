package com.davide.invoice_manager.service;

import com.davide.invoice_manager.command.CreateBusinessProfileCommand;
import com.davide.invoice_manager.command.UpdateBusinessProfileCommand;
import com.davide.invoice_manager.domain.*;
import com.davide.invoice_manager.exception.ResourceNotFoundException;
import com.davide.invoice_manager.repository.BusinessProfileRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BusinessProfileServiceTest {
    @Mock
    private BusinessProfileRepository businessProfileRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private BusinessProfileService businessProfileService;

    private User testUser;
    private BusinessProfile testBusinessProfile;
    private BusinessProfile testBusinessProfile2;
    private List<BusinessProfile> testBusinessProfiles;
    private CreateBusinessProfileCommand createBusinessProfileCommand;
    private CreateBusinessProfileCommand createBusinessProfileCommandNoUser;
    private UpdateBusinessProfileCommand updateBusinessProfileCommand;

    @BeforeEach
    public void init(){
        testUser = new User(
                1L,
                "admin",
                "admin",
                Role.ADMIN,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        testBusinessProfile = new BusinessProfile(
                1L,
                "prova",
                "ABC",
                "",
                PersonType.FISICA,
                "",
                "",
                null,
                LocalDateTime.now(),
                LocalDateTime.now()

        );
        testBusinessProfile2 = new BusinessProfile(
                2L,
                "prova2",
                "01012",
                "01012",
                PersonType.GIURIDICA,
                "",
                "",
                testUser,
                LocalDateTime.now(),
                LocalDateTime.now()

        );
        testBusinessProfiles = new ArrayList<>();
        testBusinessProfiles.add(testBusinessProfile);
        testBusinessProfiles.add(testBusinessProfile2);
        createBusinessProfileCommand = new CreateBusinessProfileCommand(
                testBusinessProfile2.getBusinessName(),
                testBusinessProfile2.getFiscalCode(),
                testBusinessProfile2.getVatCode(),
                testBusinessProfile2.getPersonType(),
                testBusinessProfile2.getPec(),
                testBusinessProfile2.getPhoneNumber(),
                testUser.getId()
        );
        createBusinessProfileCommandNoUser = new CreateBusinessProfileCommand(
                testBusinessProfile.getBusinessName(),
                testBusinessProfile.getFiscalCode(),
                testBusinessProfile.getVatCode(),
                testBusinessProfile.getPersonType(),
                testBusinessProfile.getPec(),
                testBusinessProfile.getPhoneNumber(),
                null
        );
        updateBusinessProfileCommand = new UpdateBusinessProfileCommand(
                "provaUpdate",
                "01013",
                "1234",
                PersonType.FISICA,
                "qwert",
                "0102"
        );

    }

    @Test
    public void findAll_shouldReturnAllBusinessProfiles(){
        Mockito.when(businessProfileRepository.findAll()).thenReturn(testBusinessProfiles);
        List<BusinessProfile> businessProfiles = businessProfileService.findAll();
        Assertions.assertEquals(testBusinessProfiles, businessProfiles);
    }

    @Test
    public void getBusinessProfileByUser_shouldThrowException_whenProfileNotFoundForUser(){
        Mockito.when(businessProfileRepository.findByUser(testUser)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> businessProfileService.getBusinessProfileByUser(testUser));
    }

    @Test
    public void getBusinessProfileByUser_shouldReturnBP_whenProfileExistsForUser(){
        Mockito.when(businessProfileRepository.findByUser(testUser)).thenReturn(Optional.of(testBusinessProfile2));
        BusinessProfile businessProfile = businessProfileService.getBusinessProfileByUser(testUser);
        Assertions.assertEquals(testBusinessProfile2.getId(), businessProfile.getId());
        Assertions.assertEquals(testBusinessProfile2.getBusinessName(), businessProfile.getBusinessName());
        Assertions.assertEquals(testBusinessProfile2.getFiscalCode(), businessProfile.getFiscalCode());
    }

    @Test
    public void getBusinessProfileById_shouldThrowException_whenIdNotFound(){
        Mockito.when(businessProfileRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> businessProfileService.getBusinessProfileById(1L));
    }

    @Test
    public void getBusinessProfileById_shouldReturnBP_whenIdExists(){
        Mockito.when(businessProfileRepository.findById(1L)).thenReturn(Optional.of(testBusinessProfile));
        BusinessProfile businessProfile = businessProfileService.getBusinessProfileById(1L);
        Assertions.assertSame(testBusinessProfile, businessProfile);
    }

    @Test
    public void createBusinessProfile_shouldThrowException_whenUserIdNotFound(){
        Mockito.when(userService.getUserById(createBusinessProfileCommand.userId())).thenThrow(new ResourceNotFoundException("User not found for id: " + createBusinessProfileCommand.userId()));
        Assertions.assertThrows(ResourceNotFoundException.class, () -> businessProfileService.createBusinessProfile(createBusinessProfileCommand));
        Mockito.verify(businessProfileRepository, Mockito.never()).save(Mockito.any());
    }
    @Test
    public void createBusinessProfile_shouldSaveBPWithUser_whenUserIdProvided(){
        Mockito.when(userService.getUserById(createBusinessProfileCommand.userId())).thenReturn(testUser);
        Mockito.when(businessProfileRepository.save(Mockito.any(BusinessProfile.class))).thenReturn(testBusinessProfile2);
        BusinessProfile businessProfile = businessProfileService.createBusinessProfile(createBusinessProfileCommand);
        ArgumentCaptor<BusinessProfile> captor = ArgumentCaptor.forClass(BusinessProfile.class);
        Mockito.verify(businessProfileRepository).save(captor.capture());
        BusinessProfile saved = captor.getValue();
        Assertions.assertSame(testBusinessProfile2, businessProfile);
        Assertions.assertSame(testUser, saved.getUser());
        assertFieldsMatch(createBusinessProfileCommand, saved);
    }

    @Test
    public void createBusinessProfile_shouldSaveBPWithoutUser_whenUserIdIsNull(){
        Mockito.when(businessProfileRepository.save(Mockito.any(BusinessProfile.class))).thenReturn(testBusinessProfile);
        BusinessProfile businessProfile = businessProfileService.createBusinessProfile(createBusinessProfileCommandNoUser);
        ArgumentCaptor<BusinessProfile> captor = ArgumentCaptor.forClass(BusinessProfile.class);
        Mockito.verify(businessProfileRepository).save(captor.capture());
        BusinessProfile saved = captor.getValue();
        Assertions.assertNull(saved.getUser());
        Assertions.assertSame(testBusinessProfile, businessProfile);
        assertFieldsMatch(createBusinessProfileCommandNoUser, saved);
        Mockito.verify(userService, Mockito.never()).getUserById(Mockito.any());
    }

    @Test
    public void updateBusinessProfile_shouldThrowException_whenIdNotFound(){
        Mockito.when(businessProfileRepository.findById(testBusinessProfile.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> businessProfileService.updateBusinessProfile(testBusinessProfile.getId(), updateBusinessProfileCommand));
        Mockito.verify(businessProfileRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void updateBusinessProfile_shouldUpdateAllFields_whenAllFieldsProvided(){
        Mockito.when(businessProfileRepository.findById(testBusinessProfile2.getId())).thenReturn(Optional.of(testBusinessProfile2));
        Mockito.when(businessProfileRepository.save(testBusinessProfile2)).thenReturn(testBusinessProfile2);
        BusinessProfile businessProfile = businessProfileService.updateBusinessProfile(testBusinessProfile2.getId(), updateBusinessProfileCommand);
        Assertions.assertEquals(updateBusinessProfileCommand.businessName(), businessProfile.getBusinessName());
        Assertions.assertEquals(updateBusinessProfileCommand.fiscalCode(), businessProfile.getFiscalCode());
        Assertions.assertEquals(updateBusinessProfileCommand.vatCode(), businessProfile.getVatCode());
        Assertions.assertEquals(updateBusinessProfileCommand.personType(), businessProfile.getPersonType());
        Assertions.assertEquals(updateBusinessProfileCommand.phoneNumber(), businessProfile.getPhoneNumber());
        Assertions.assertEquals(updateBusinessProfileCommand.pec(), businessProfile.getPec());
    }

    @Test
    public void updateBusinessProfile_shouldNotUpdateAnyField_whenNoFieldsProvided(){
        String businessNameOG = testBusinessProfile2.getBusinessName();
        String fiscalCodeOG = testBusinessProfile2.getFiscalCode();
        String vatCodeOG = testBusinessProfile2.getVatCode();
        PersonType personTypeOG = testBusinessProfile2.getPersonType();
        String phoneNumberOG = testBusinessProfile2.getPhoneNumber();
        String pecOG = testBusinessProfile2.getPec();
        Mockito.when(businessProfileRepository.findById(testBusinessProfile2.getId())).thenReturn(Optional.of(testBusinessProfile2));
        Mockito.when(businessProfileRepository.save(testBusinessProfile2)).thenReturn(testBusinessProfile2);
        BusinessProfile businessProfile = businessProfileService.updateBusinessProfile(testBusinessProfile2.getId(), new UpdateBusinessProfileCommand(null, null, null, null, null, null));
        Assertions.assertEquals(businessNameOG, businessProfile.getBusinessName());
        Assertions.assertEquals(fiscalCodeOG, businessProfile.getFiscalCode());
        Assertions.assertEquals(vatCodeOG, businessProfile.getVatCode());
        Assertions.assertEquals(personTypeOG, businessProfile.getPersonType());
        Assertions.assertEquals(phoneNumberOG, businessProfile.getPhoneNumber());
        Assertions.assertEquals(pecOG, businessProfile.getPec());
    }

    @Test
    public void deleteBusinessProfile_shouldThrowException_whenBPDoesNotExist(){
        Mockito.when(businessProfileRepository.findById(testBusinessProfile.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> businessProfileService.deleteBusinessProfile(testBusinessProfile.getId()));
        Mockito.verify(businessProfileRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    public void deleteBusinessProfile_shouldDeleteBP_whenBPExists(){
        Mockito.when(businessProfileRepository.findById(testBusinessProfile.getId())).thenReturn(Optional.of(testBusinessProfile));
        businessProfileService.deleteBusinessProfile(testBusinessProfile.getId());
        Mockito.verify(businessProfileRepository).delete(testBusinessProfile);
    }

    private void assertFieldsMatch(CreateBusinessProfileCommand command, BusinessProfile bp) {
        Assertions.assertEquals(command.businessName(), bp.getBusinessName());
        Assertions.assertEquals(command.fiscalCode(), bp.getFiscalCode());
        Assertions.assertEquals(command.vatCode(), bp.getVatCode());
        Assertions.assertEquals(command.personType(), bp.getPersonType());
        Assertions.assertEquals(command.phoneNumber(), bp.getPhoneNumber());
        Assertions.assertEquals(command.pec(), bp.getPec());
    }

}
