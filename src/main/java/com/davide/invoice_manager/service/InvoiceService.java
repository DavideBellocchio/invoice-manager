package com.davide.invoice_manager.service;

import com.davide.invoice_manager.domain.BusinessProfile;
import com.davide.invoice_manager.domain.Invoice;
import com.davide.invoice_manager.domain.InvoiceStatus;
import com.davide.invoice_manager.domain.User;
import com.davide.invoice_manager.exception.ResourceNotFoundException;
import com.davide.invoice_manager.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserService userService;
    private final BusinessProfileService businessProfileService;

    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }
    public Invoice findById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Invoice not found with id: " + id ));
    }
    public List<Invoice> findBySender(User sender) {
        return invoiceRepository.findBySender(sender);
    }
    public List<Invoice> findByRecipient(BusinessProfile recipient) {
        return invoiceRepository.findByRecipient(recipient);
    }
    public List<Invoice> findByStatus(InvoiceStatus status) {
        return invoiceRepository.findByStatus(status);
    }
    public List<Invoice> findBySenderAndStatus(User sender, InvoiceStatus status) {
        return invoiceRepository.findBySenderAndStatus(sender, status);
    }
    public Invoice createInvoice(Invoice invoice) {
        userService.getUserById(invoice.getSender().getId());
        businessProfileService.getBusinessProfileById(invoice.getRecipient().getId());
        return invoiceRepository.save(invoice);
    }
    public Invoice updateInvoice(Invoice invoice) {
        Invoice i = findById(invoice.getId());
        validateIsDraft(i);
        if(invoice.getSender() != null) {
            userService.getUserById(invoice.getSender().getId());
            i.setSender(invoice.getSender());
        }
        if(invoice.getRecipient() != null) {
            businessProfileService.getBusinessProfileById(invoice.getRecipient().getId());
            i.setRecipient(invoice.getRecipient());
        }
        if(invoice.getIssueDate() != null) {
            i.setIssueDate(invoice.getIssueDate());
        }
        if(invoice.getDueDate() != null) {
            i.setDueDate(invoice.getDueDate());
        }
        return invoiceRepository.save(i);
    }
    public void deleteById(Long id) {
        Invoice invoice = findById(id);
        validateIsDraft(invoice);
        invoiceRepository.delete(invoice);
    }
    public Invoice issueInvoice(Long id) {
        Invoice invoice = findById(id);
        validateIsDraft(invoice);
        invoice.setStatus(InvoiceStatus.ISSUED);
        return invoiceRepository.save(invoice);
    }
    public Invoice payInvoice(Long id) {
        Invoice invoice = findById(id);
        validateIsIssued(invoice);
        invoice.setStatus(InvoiceStatus.PAID);
        return invoiceRepository.save(invoice);
    }
    public Invoice markAsOverdue(Long id) {
        Invoice invoice = findById(id);
        validateIsIssued(invoice);
        invoice.setStatus(InvoiceStatus.OVERDUE);
        return invoiceRepository.save(invoice);
    }
    public void validateIsDraft(Invoice invoice) {
        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Invoice status must be DRAFT");
        }
    }
    public void validateIsIssued(Invoice invoice) {
        if(invoice.getStatus() != InvoiceStatus.ISSUED) {
            throw new IllegalStateException("Invoice status is " + invoice.getStatus() + ", must be issued");
        }
    }
}

