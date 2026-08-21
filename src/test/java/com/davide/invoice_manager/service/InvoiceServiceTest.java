package com.davide.invoice_manager.service;

import com.davide.invoice_manager.command.CreateInvoiceCommand;
import com.davide.invoice_manager.command.UpdateInvoiceCommand;
import com.davide.invoice_manager.domain.*;
import com.davide.invoice_manager.exception.ResourceNotFoundException;
import com.davide.invoice_manager.repository.InvoiceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private UserService userService;
    @Mock
    private BusinessProfileService businessProfileService;
    @InjectMocks
    private InvoiceService invoiceService;

    private User testUser;
    private BusinessProfile originalRecipient;
    private BusinessProfile newRecipient;
    private Invoice draftInvoice;
    private Invoice issuedInvoice;
    private List<Invoice> invoicesTest;
    private CreateInvoiceCommand createInvoiceCommand;
    private UpdateInvoiceCommand fullUpdateCommand;
    private UpdateInvoiceCommand emptyUpdateCommand;

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
        originalRecipient = new BusinessProfile(
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
        newRecipient = new BusinessProfile(
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
        draftInvoice = new Invoice(
                1L,
                testUser,
                originalRecipient,
                InvoiceStatus.DRAFT,
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        issuedInvoice = new Invoice(
                2L,
                testUser,
                originalRecipient,
                InvoiceStatus.ISSUED,
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        createInvoiceCommand = new CreateInvoiceCommand(
                testUser.getId(),
                originalRecipient.getId(),
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );
        fullUpdateCommand = new UpdateInvoiceCommand(
                newRecipient.getId(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(60)
        );
        emptyUpdateCommand = new UpdateInvoiceCommand(null,null,null);
    }

    @Test
    public void findAll_shouldReturnAllInvoices(){
        invoicesTest = List.of(draftInvoice, issuedInvoice);
        Mockito.when(invoiceRepository.findAll()).thenReturn(invoicesTest);
        List<Invoice> result = invoiceService.findAll();
        Assertions.assertSame(invoicesTest, result);
    }

    @Test
    public void findById_shouldThrowException_whenIdNotFound(){
        Mockito.when(invoiceRepository.findById(draftInvoice.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> invoiceService.findById(draftInvoice.getId()));
    }

    @Test
    public void findById_shouldReturnInvoice_whenIdExists(){
        Mockito.when(invoiceRepository.findById(draftInvoice.getId())).thenReturn(Optional.of(draftInvoice));
        Invoice result = invoiceService.findById(draftInvoice.getId());
        Assertions.assertSame(draftInvoice, result);
    }

    @Test
    public void findBySender_shouldReturnInvoicesBySender(){
        invoicesTest = List.of(draftInvoice, issuedInvoice);
        Mockito.when(invoiceRepository.findBySender(testUser)).thenReturn(invoicesTest);
        List<Invoice> result = invoiceService.findBySender(testUser);
        Assertions.assertSame(invoicesTest, result);
    }

    @Test
    public void findByRecipient_shouldReturnInvoicesOfRecipient(){
        invoicesTest = List.of(draftInvoice);
        Mockito.when(invoiceRepository.findByRecipient(originalRecipient)).thenReturn(invoicesTest);
        List<Invoice> result = invoiceService.findByRecipient(originalRecipient);
        Assertions.assertSame(invoicesTest, result);
    }

    @Test
    public void findByStatus_shouldReturnInvoicesWithGivenStatus(){
        invoicesTest = List.of(draftInvoice);
        Mockito.when(invoiceRepository.findByStatus(InvoiceStatus.DRAFT)).thenReturn(invoicesTest);
        List<Invoice> result = invoiceService.findByStatus(InvoiceStatus.DRAFT);
        Assertions.assertSame(invoicesTest, result);
    }

    @Test
    public void findBySenderAndStatus_shouldReturnInvoicesOfSenderWithGivenStatus(){
        invoicesTest = List.of(draftInvoice);
        Mockito.when(invoiceRepository.findBySenderAndStatus(testUser, InvoiceStatus.DRAFT)).thenReturn(invoicesTest);
        List<Invoice> result = invoiceService.findBySenderAndStatus(testUser, InvoiceStatus.DRAFT);
        Assertions.assertSame(invoicesTest, result);
    }

    @Test
    public void createInvoice_shouldThrowException_whenSenderNotFound(){
        Mockito.when(userService.getUserById(createInvoiceCommand.senderId())).thenThrow(new ResourceNotFoundException("Sender not found"));
        Assertions.assertThrows(ResourceNotFoundException.class, () -> invoiceService.createInvoice(createInvoiceCommand));
        Mockito.verify(businessProfileService, Mockito.never()).getBusinessProfileById(Mockito.any());
        Mockito.verify(invoiceRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void createInvoice_shouldThrowException_whenRecipientNotFound(){
        Mockito.when(userService.getUserById(createInvoiceCommand.senderId())).thenReturn(testUser);
        Mockito.when(businessProfileService.getBusinessProfileById(createInvoiceCommand.recipientId())).thenThrow(new ResourceNotFoundException("Recipient not found"));
        Assertions.assertThrows(ResourceNotFoundException.class, () -> invoiceService.createInvoice(createInvoiceCommand));
        Mockito.verify(invoiceRepository, Mockito.never()).save(Mockito.any());
    }
    @Test
    public void createInvoice_shouldSaveInvoiceWithDraftStatus_whenSenderAndRecipientExist(){
        Mockito.when(userService.getUserById(createInvoiceCommand.senderId())).thenReturn(testUser);
        Mockito.when(businessProfileService.getBusinessProfileById(createInvoiceCommand.recipientId())).thenReturn(originalRecipient);
        Mockito.when(invoiceRepository.save(Mockito.any(Invoice.class))).thenReturn(draftInvoice);
        Invoice result = invoiceService.createInvoice(createInvoiceCommand);
        ArgumentCaptor<Invoice> invoiceArgumentCaptor = ArgumentCaptor.forClass(Invoice.class);
        Mockito.verify(invoiceRepository).save(invoiceArgumentCaptor.capture());
        Invoice saved = invoiceArgumentCaptor.getValue();
        Assertions.assertSame(draftInvoice, result);
        Assertions.assertSame(testUser, saved.getSender());
        Assertions.assertSame(originalRecipient, saved.getRecipient());
        Assertions.assertEquals(InvoiceStatus.DRAFT, saved.getStatus());
        Assertions.assertEquals(createInvoiceCommand.dueDate(), saved.getDueDate());
        Assertions.assertEquals(createInvoiceCommand.issueDate(), saved.getIssueDate());
    }

    @Test
    public void updateInvoice_shouldThrowException_whenInvoiceNotFound(){
        Mockito.when(invoiceRepository.findById(draftInvoice.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> invoiceService.updateInvoice(draftInvoice.getId(), fullUpdateCommand));
        Mockito.verify(businessProfileService, Mockito.never()).getBusinessProfileById(Mockito.any());
        Mockito.verify(invoiceRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void updateInvoice_shouldThrowException_whenInvoiceStatusNotDraft(){
        Mockito.when(invoiceRepository.findById(issuedInvoice.getId())).thenReturn(Optional.of(issuedInvoice));
        Assertions.assertThrows(IllegalStateException.class, () -> invoiceService.updateInvoice(issuedInvoice.getId(), fullUpdateCommand));
        Mockito.verify(businessProfileService, Mockito.never()).getBusinessProfileById(Mockito.any());
        Mockito.verify(invoiceRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void updateInvoice_shouldThrowException_whenRecipientToUpdateNotFound(){
        Mockito.when(invoiceRepository.findById(draftInvoice.getId())).thenReturn(Optional.of(draftInvoice));
        Mockito.when(businessProfileService.getBusinessProfileById(fullUpdateCommand.recipientId())).thenThrow(new ResourceNotFoundException("Recipient not found"));
        Assertions.assertThrows(ResourceNotFoundException.class, () -> invoiceService.updateInvoice(draftInvoice.getId(), fullUpdateCommand));
        Mockito.verify(invoiceRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void updateInvoice_shouldNotUpdateAnyField_whenNoFieldsProvided(){
        BusinessProfile recipientOG = draftInvoice.getRecipient();
        LocalDate issueDateOG = draftInvoice.getIssueDate();
        LocalDate dueDateOG = draftInvoice.getDueDate();
        Mockito.when(invoiceRepository.findById(draftInvoice.getId())).thenReturn(Optional.of(draftInvoice));
        Mockito.when(invoiceRepository.save(Mockito.any(Invoice.class))).thenReturn(draftInvoice);
        Invoice result = invoiceService.updateInvoice(draftInvoice.getId(), emptyUpdateCommand);
        Assertions.assertSame(recipientOG, result.getRecipient());
        Assertions.assertEquals(dueDateOG, result.getDueDate());
        Assertions.assertEquals(issueDateOG, result.getIssueDate());
        Mockito.verify(businessProfileService, Mockito.never()).getBusinessProfileById(Mockito.any());
    }

    @Test
    public void updateInvoice_shouldUpdateAllFields_whenAllFieldsProvided(){
        Mockito.when(invoiceRepository.findById(draftInvoice.getId())).thenReturn(Optional.of(draftInvoice));
        Mockito.when(businessProfileService.getBusinessProfileById(fullUpdateCommand.recipientId())).thenReturn(newRecipient);
        Mockito.when(invoiceRepository.save(draftInvoice)).thenReturn(draftInvoice);
        Invoice result = invoiceService.updateInvoice(draftInvoice.getId(), fullUpdateCommand);
        Assertions.assertSame(newRecipient, result.getRecipient());
        Assertions.assertEquals(fullUpdateCommand.dueDate(), result.getDueDate());
        Assertions.assertEquals(fullUpdateCommand.issueDate(), result.getIssueDate());
    }

    @Test
    public void deleteById_shouldThrowException_whenIdNotFound(){
        Mockito.when(invoiceRepository.findById(draftInvoice.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> invoiceService.deleteById(draftInvoice.getId()));
        Mockito.verify(invoiceRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    public void deleteById_shouldThrowException_whenInvoiceStatusNotDraft(){
        Mockito.when(invoiceRepository.findById(issuedInvoice.getId())).thenReturn(Optional.of(issuedInvoice));
        Assertions.assertThrows(IllegalStateException.class, () -> invoiceService.deleteById(issuedInvoice.getId()));
        Mockito.verify(invoiceRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    public void deleteById_shouldDeleteInvoice_whenIdExistAndStatusDraft(){
        Mockito.when(invoiceRepository.findById(draftInvoice.getId())).thenReturn(Optional.of(draftInvoice));
        invoiceService.deleteById(draftInvoice.getId());
        Mockito.verify(invoiceRepository, Mockito.times(1)).delete(draftInvoice);
    }

    @Test
    public void issueInvoice_shouldThrowException_whenIdNotFound(){
        Mockito.when(invoiceRepository.findById(draftInvoice.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> invoiceService.issueInvoice(draftInvoice.getId()));
        Mockito.verify(invoiceRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void issueInvoice_shouldThrowException_whenInvoiceStatusNotDraft(){
        Mockito.when(invoiceRepository.findById(issuedInvoice.getId())).thenReturn(Optional.of(issuedInvoice));
        Assertions.assertThrows(IllegalStateException.class, () -> invoiceService.issueInvoice(issuedInvoice.getId()));
        Mockito.verify(invoiceRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void issueInvoice_shouldSetStatusToIssued_whenInvoiceIsDraft(){
        Mockito.when(invoiceRepository.findById(draftInvoice.getId())).thenReturn(Optional.of(draftInvoice));
        Mockito.when(invoiceRepository.save(draftInvoice)).thenReturn(draftInvoice);
        Invoice invoice = invoiceService.issueInvoice(draftInvoice.getId());
        Assertions.assertSame(draftInvoice, invoice);
        Assertions.assertEquals(InvoiceStatus.ISSUED, invoice.getStatus());
    }

    @Test
    public void payInvoice_shouldThrowException_whenIdNotFound(){
        Mockito.when(invoiceRepository.findById(issuedInvoice.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> invoiceService.payInvoice(issuedInvoice.getId()));
        Mockito.verify(invoiceRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void payInvoice_shouldThrowException_whenInvoiceStatusNotIssued(){
        Mockito.when(invoiceRepository.findById(draftInvoice.getId())).thenReturn(Optional.of(draftInvoice));
        Assertions.assertThrows(IllegalStateException.class, () -> invoiceService.payInvoice(draftInvoice.getId()));
        Mockito.verify(invoiceRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void payInvoice_shouldSetStatusToPaid_whenInvoiceIsIssued(){
        Mockito.when(invoiceRepository.findById(issuedInvoice.getId())).thenReturn(Optional.of(issuedInvoice));
        Mockito.when(invoiceRepository.save(issuedInvoice)).thenReturn(issuedInvoice);
        Invoice invoice = invoiceService.payInvoice(issuedInvoice.getId());
        Assertions.assertSame(issuedInvoice, invoice);
        Assertions.assertEquals(InvoiceStatus.PAID, invoice.getStatus());
    }

    @Test
    public void markAsOverdue_shouldThrowException_whenIdNotFound(){
        Mockito.when(invoiceRepository.findById(issuedInvoice.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> invoiceService.markAsOverdue(issuedInvoice.getId()));
        Mockito.verify(invoiceRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void markAsOverdue_shouldThrowException_whenInvoiceStatusNotIssued(){
        Mockito.when(invoiceRepository.findById(draftInvoice.getId())).thenReturn(Optional.of(draftInvoice));
        Assertions.assertThrows(IllegalStateException.class, () -> invoiceService.markAsOverdue(draftInvoice.getId()));
        Mockito.verify(invoiceRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void markAsOverdue_shouldSetStatusToOverdue_whenInvoiceIsIssued(){
        Mockito.when(invoiceRepository.findById(issuedInvoice.getId())).thenReturn(Optional.of(issuedInvoice));
        Mockito.when(invoiceRepository.save(issuedInvoice)).thenReturn(issuedInvoice);
        Invoice invoice = invoiceService.markAsOverdue(issuedInvoice.getId());
        Assertions.assertSame(issuedInvoice, invoice);
        Assertions.assertEquals(InvoiceStatus.OVERDUE, invoice.getStatus());
    }

    @Test
    public void validateIsDraft_shouldThrowException_whenInvoiceStatusNotDraft(){
        Assertions.assertThrows(IllegalStateException.class, () -> invoiceService.validateIsDraft(issuedInvoice));
    }

    @Test
    public void validateIsDraft_shouldNotThrowException_whenInvoiceStatusDraft(){
        Assertions.assertDoesNotThrow(() -> invoiceService.validateIsDraft(draftInvoice));
    }
}
