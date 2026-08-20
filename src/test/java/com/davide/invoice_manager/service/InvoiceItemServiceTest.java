package com.davide.invoice_manager.service;


import com.davide.invoice_manager.command.CreateInvoiceItemCommand;
import com.davide.invoice_manager.command.UpdateInvoiceItemCommand;
import com.davide.invoice_manager.domain.*;
import com.davide.invoice_manager.exception.ResourceNotFoundException;
import com.davide.invoice_manager.repository.InvoiceItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class InvoiceItemServiceTest {

    @Mock
    private InvoiceItemRepository invoiceItemRepository;
    @Mock
    private InvoiceService invoiceService;
    @Mock
    private ProductService productService;
    @InjectMocks
    private InvoiceItemService invoiceItemService;

    private User userTest;
    private BusinessProfile businessProfileTest;
    private Product productTest;
    private Invoice invoiceTestDraft;
    private Invoice invoiceTestNoDraft;
    private InvoiceItem invoiceItemTestDraft;
    private InvoiceItem invoiceItemTestNoDraft;
    private List<InvoiceItem> invoiceItemsTest;
    private List<InvoiceItem> invoiceItemsSameInvoiceTest;
    private CreateInvoiceItemCommand createCommandDraft;
    private CreateInvoiceItemCommand createCommandNoDraft;
    private UpdateInvoiceItemCommand updateCommand;

    @BeforeEach
    public void init(){

        productTest = new Product(
                1L,
                "prodottoTest",
                "descTest",
                new BigDecimal("10"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        userTest = new User(
                1L,
                "admin",
                "admin",
                Role.ADMIN,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        businessProfileTest = new BusinessProfile(
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
        invoiceTestDraft = new Invoice(
                1L,
                userTest,
                businessProfileTest,
                InvoiceStatus.DRAFT,
                LocalDate.now(),
                LocalDate.now(),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        invoiceTestNoDraft = new Invoice(
                2L,
                userTest,
                businessProfileTest,
                InvoiceStatus.ISSUED,
                LocalDate.now(),
                LocalDate.now(),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        invoiceItemTestDraft = new InvoiceItem(
                1L,
                1,
                productTest.getPrice(),
                productTest,
                invoiceTestDraft,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        invoiceItemTestNoDraft = new InvoiceItem(
                2L,
                1,
                productTest.getPrice(),
                productTest,
                invoiceTestNoDraft,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        invoiceItemsTest = List.of(invoiceItemTestDraft, invoiceItemTestNoDraft);
        invoiceItemsSameInvoiceTest = List.of(invoiceItemTestDraft);
        createCommandDraft = new CreateInvoiceItemCommand(
                invoiceTestDraft.getId(),
                productTest.getId(),
                3
        );
        createCommandNoDraft = new CreateInvoiceItemCommand(
                invoiceTestNoDraft.getId(),
                productTest.getId(),
                3
        );
        updateCommand = new UpdateInvoiceItemCommand(20);
    }
    @Test
    public void findAll_shouldReturnAllInvoiceItems(){
        Mockito.when(invoiceItemRepository.findAll()).thenReturn(invoiceItemsTest);
        List<InvoiceItem> invoiceItems = invoiceItemService.findAll();
        Assertions.assertSame(invoiceItemsTest, invoiceItems);
    }
    @Test
    public void findAllByInvoice_shouldReturnAllInvoiceItemsByInvoice(){
        Mockito.when(invoiceItemRepository.findByInvoice(invoiceTestDraft)).thenReturn(invoiceItemsSameInvoiceTest);
        List<InvoiceItem> invoiceItems = invoiceItemService.findAllByInvoice(invoiceTestDraft);
        Assertions.assertSame(invoiceItemsSameInvoiceTest, invoiceItems);
    }

    @Test
    public void findById_shouldThrowException_whenIdNotFound(){
        Mockito.when(invoiceItemRepository.findById(invoiceItemTestDraft.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> invoiceItemService.findById(invoiceItemTestDraft.getId()));
    }

    @Test
    public void findById_shouldReturnInvoiceItem_whenIdExists(){
        Mockito.when(invoiceItemRepository.findById(invoiceItemTestDraft.getId())).thenReturn(Optional.of(invoiceItemTestDraft));
        InvoiceItem invoiceItem = invoiceItemService.findById(invoiceItemTestDraft.getId());
        Assertions.assertSame(invoiceItemTestDraft, invoiceItem);
    }

    @Test
    public void addInvoiceItem_shouldThrowException_whenInvoiceIdNotFound(){
        Mockito.when(invoiceService.findById(invoiceTestDraft.getId())).thenThrow(new ResourceNotFoundException("InvoiceItem not found for id: " + invoiceTestDraft.getId()));
        Assertions.assertThrows(ResourceNotFoundException.class, () -> invoiceItemService.addInvoiceItem(createCommandDraft));
        Mockito.verify(invoiceItemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void addInvoiceItem_shouldThrowException_whenInvoiceStatusNotDraft(){
        Mockito.when(invoiceService.findById(invoiceTestNoDraft.getId())).thenReturn(invoiceTestNoDraft);
        Mockito.doThrow(new IllegalStateException("Invoice status must be DRAFT")).when(invoiceService).validateIsDraft(invoiceTestNoDraft);
        Assertions.assertThrows(IllegalStateException.class, () -> invoiceItemService.addInvoiceItem(createCommandNoDraft));
        Mockito.verify(invoiceItemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void addInvoiceItem_shouldThrowException_whenProductIdNotFound(){
        Mockito.when(invoiceService.findById(invoiceTestDraft.getId())).thenReturn(invoiceTestDraft);
        Mockito.when(productService.findById(productTest.getId())).thenThrow(new ResourceNotFoundException("Product not found for id: " + productTest.getId()));
        Assertions.assertThrows(ResourceNotFoundException.class, () -> invoiceItemService.addInvoiceItem(createCommandDraft));
        Mockito.verify(invoiceItemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void addInvoiceItem_shouldSaveItemWithFrozenProductPrice_whenInvoiceIsDraft(){
        Mockito.when(invoiceService.findById(invoiceTestDraft.getId())).thenReturn(invoiceTestDraft);
        Mockito.when(productService.findById(productTest.getId())).thenReturn(productTest);
        Mockito.when(invoiceItemRepository.save(Mockito.any(InvoiceItem.class))).thenReturn(invoiceItemTestDraft);
        InvoiceItem invoiceItem = invoiceItemService.addInvoiceItem(createCommandDraft);
        ArgumentCaptor<InvoiceItem> captor = ArgumentCaptor.forClass(InvoiceItem.class);
        Mockito.verify(invoiceItemRepository).save(captor.capture());
        InvoiceItem saved = captor.getValue();
        Assertions.assertSame(invoiceItemTestDraft, invoiceItem);
        Assertions.assertSame(invoiceTestDraft,saved.getInvoice());
        Assertions.assertSame(productTest, saved.getProduct());
        Assertions.assertEquals(createCommandDraft.quantity(), saved.getQuantity());
        Assertions.assertEquals(0, saved.getPrice().compareTo(productTest.getPrice()));
    }

    @Test
    public void updateInvoiceItem_shouldThrowException_whenItemIdNotFound(){
        Mockito.when(invoiceItemRepository.findById(invoiceItemTestDraft.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> invoiceItemService.updateInvoiceItem(invoiceItemTestDraft.getId(), updateCommand));
        Mockito.verify(invoiceItemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void updateInvoiceItem_shouldThrowException_whenInvoiceStatusNotDraft(){
        Mockito.when(invoiceItemRepository.findById(invoiceItemTestNoDraft.getId())).thenReturn(Optional.of(invoiceItemTestNoDraft));
        Mockito.doThrow(new IllegalStateException("Invoice status must be DRAFT")).when(invoiceService).validateIsDraft(invoiceItemTestNoDraft.getInvoice());
        Assertions.assertThrows(IllegalStateException.class, () -> invoiceItemService.updateInvoiceItem(invoiceItemTestNoDraft.getId(), updateCommand));
        Mockito.verify(invoiceItemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void updateInvoiceItem_shouldUpdateQuantity_whenQuantityProvided(){
        Mockito.when(invoiceItemRepository.findById(invoiceItemTestDraft.getId())).thenReturn(Optional.of(invoiceItemTestDraft));
        Mockito.when(invoiceItemRepository.save(invoiceItemTestDraft)).thenReturn(invoiceItemTestDraft);
        InvoiceItem invoiceItem = invoiceItemService.updateInvoiceItem(invoiceItemTestDraft.getId(), updateCommand);
        Assertions.assertEquals(updateCommand.quantity(), invoiceItem.getQuantity());
    }

    @Test
    public void updateInvoiceItem_shouldNotUpdateQuantity_whenQuantityNotProvided(){
        Integer quantity = invoiceItemTestDraft.getQuantity();
        Mockito.when(invoiceItemRepository.findById(invoiceItemTestDraft.getId())).thenReturn(Optional.of(invoiceItemTestDraft));
        Mockito.when(invoiceItemRepository.save(invoiceItemTestDraft)).thenReturn(invoiceItemTestDraft);
        InvoiceItem invoiceItem = invoiceItemService.updateInvoiceItem(invoiceItemTestDraft.getId(), new UpdateInvoiceItemCommand(null));
        Assertions.assertEquals(quantity, invoiceItem.getQuantity());
    }

    @Test
    public void deleteInvoiceItem_shouldThrowException_whenItemIdNotFound(){
        Mockito.when(invoiceItemRepository.findById(invoiceItemTestDraft.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> invoiceItemService.deleteInvoiceItem(invoiceItemTestDraft.getId()));
        Mockito.verify(invoiceItemRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    public void deleteInvoiceItem_shouldThrowException_whenInvoiceStatusNotDraft(){
        Mockito.when(invoiceItemRepository.findById(invoiceItemTestNoDraft.getId())).thenReturn(Optional.of(invoiceItemTestNoDraft));
        Mockito.doThrow(new IllegalStateException("Invoice status must be DRAFT")).when(invoiceService).validateIsDraft(invoiceItemTestNoDraft.getInvoice());
        Assertions.assertThrows(IllegalStateException.class, () -> invoiceItemService.deleteInvoiceItem(invoiceItemTestNoDraft.getId()));
        Mockito.verify(invoiceItemRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    public void deleteInvoiceItem_shouldDeleteInvoiceItem(){
        Mockito.when(invoiceItemRepository.findById(invoiceItemTestDraft.getId())).thenReturn(Optional.of(invoiceItemTestDraft));
        invoiceItemService.deleteInvoiceItem(invoiceItemTestDraft.getId());
        Mockito.verify(invoiceItemRepository).delete(invoiceItemTestDraft);
    }
}
