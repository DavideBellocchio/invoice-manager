package com.davide.invoice_manager.service;


import com.davide.invoice_manager.command.CreateInvoiceCommand;
import com.davide.invoice_manager.command.CreateInvoiceItemCommand;
import com.davide.invoice_manager.command.CreateInvoiceWithItemsCommand;
import com.davide.invoice_manager.command.InvoiceItemRequest;
import com.davide.invoice_manager.domain.*;
import com.davide.invoice_manager.exception.ResourceNotFoundException;
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

@ExtendWith(MockitoExtension.class)
public class InvoiceCreationServiceTest {

    @Mock
    private InvoiceService invoiceService;
    @Mock
    private InvoiceItemService invoiceItemService;

    @InjectMocks
    private InvoiceCreationService invoiceCreationService;

    private static final Long SENDER_ID = 1L;
    private static final Long RECIPIENT_ID = 2L;
    private static final Long PRODUCT_ID1 = 1L;
    private static final Long PRODUCT_ID2 = 2L;
    private static final Long PRODUCT_ID3 = 3L;
    private Invoice invoice;
    private List<InvoiceItemRequest> items;
    private List<InvoiceItemRequest> itemsEmpty;
    private CreateInvoiceWithItemsCommand createInvoiceWithItemsCommandFull;
    private CreateInvoiceWithItemsCommand createInvoiceWithItemsCommandEmpty;
    private CreateInvoiceWithItemsCommand createInvoiceWithItemsCommandNull;

    @BeforeEach
    public void init(){

        invoice = new Invoice(
                1L,
                null,
                null,
                InvoiceStatus.DRAFT,
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        items = List.of(new InvoiceItemRequest(PRODUCT_ID1,3),new InvoiceItemRequest(PRODUCT_ID2,2),new InvoiceItemRequest(PRODUCT_ID3,7));
        itemsEmpty = List.of();
        createInvoiceWithItemsCommandFull = new CreateInvoiceWithItemsCommand(
                SENDER_ID,
                RECIPIENT_ID,
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                items
        );

    }

    @Test
    public void createInvoiceWithItems_shouldThrowException_whenItemsIsEmpty(){
        createInvoiceWithItemsCommandEmpty = new CreateInvoiceWithItemsCommand(
                SENDER_ID,
                RECIPIENT_ID,
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                itemsEmpty
        );
        Assertions.assertThrows(IllegalArgumentException.class, () -> invoiceCreationService.createInvoiceWithItems(createInvoiceWithItemsCommandEmpty));
        Mockito.verifyNoInteractions(invoiceItemService, invoiceService);
    }

    @Test
    public void createInvoiceWithItems_shouldThrowException_whenItemsIsNull(){
        createInvoiceWithItemsCommandNull = new CreateInvoiceWithItemsCommand(
                SENDER_ID,
                RECIPIENT_ID,
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                null
        );
        Assertions.assertThrows(IllegalArgumentException.class, () -> invoiceCreationService.createInvoiceWithItems(createInvoiceWithItemsCommandNull));
        Mockito.verifyNoInteractions(invoiceItemService, invoiceService);
    }

    @Test
    public void createInvoiceWithItems_shouldNotAddAnyItem_whenInvoiceCreationFails(){
        Mockito.when(invoiceService.createInvoice(Mockito.any())).thenThrow(new ResourceNotFoundException("User not found with id: 1"));
        Assertions.assertThrows(ResourceNotFoundException.class, () -> invoiceCreationService.createInvoiceWithItems(createInvoiceWithItemsCommandFull));
        Mockito.verifyNoInteractions(invoiceItemService);
    }

    @Test
    public void createInvoiceWithItems_shouldStopAddingItems_whenOneItemFails(){
        Mockito.when(invoiceService.createInvoice(Mockito.any())).thenReturn(invoice);
        Mockito.when(invoiceItemService.addInvoiceItem(Mockito.any())).thenReturn(null).thenThrow(new ResourceNotFoundException("Product not found with id: 99"));
        Assertions.assertThrows(ResourceNotFoundException.class, () -> invoiceCreationService.createInvoiceWithItems(createInvoiceWithItemsCommandFull));
        Mockito.verify(invoiceItemService, Mockito.times(2)).addInvoiceItem(Mockito.any());
        Mockito.verify(invoiceItemService, Mockito.never()).findAllByInvoice(invoice);
    }

    @Test
    public void createInvoiceWithItems_shouldCreateInvoiceAndAddAllItems(){
        List<InvoiceItem> savedItems = List.of(new InvoiceItem(), new InvoiceItem(), new InvoiceItem());
        Mockito.when(invoiceService.createInvoice(Mockito.any())).thenReturn(invoice);
        Mockito.when(invoiceItemService.findAllByInvoice(invoice)).thenReturn(savedItems);
        Invoice result = invoiceCreationService.createInvoiceWithItems(createInvoiceWithItemsCommandFull);
        Mockito.verify(invoiceService).createInvoice(new CreateInvoiceCommand(SENDER_ID,RECIPIENT_ID,createInvoiceWithItemsCommandFull.issueDate(),createInvoiceWithItemsCommandFull.dueDate()));
        ArgumentCaptor<CreateInvoiceItemCommand> captor = ArgumentCaptor.forClass(CreateInvoiceItemCommand.class);
        Mockito.verify(invoiceItemService,Mockito.times(3)).addInvoiceItem(captor.capture());
        Assertions.assertEquals(List.of(
                new CreateInvoiceItemCommand(invoice.getId(), PRODUCT_ID1,3),
                new CreateInvoiceItemCommand(invoice.getId(), PRODUCT_ID2,2),
                new CreateInvoiceItemCommand(invoice.getId(), PRODUCT_ID3,7)
            ), captor.getAllValues()
        );
        Assertions.assertSame(savedItems, result.getItems());
        Assertions.assertSame(invoice, result);
    }
}
