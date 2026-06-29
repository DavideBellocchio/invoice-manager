package com.davide.invoice_manager.repository;

import com.davide.invoice_manager.domain.BusinessProfile;
import com.davide.invoice_manager.domain.Invoice;
import com.davide.invoice_manager.domain.InvoiceStatus;
import com.davide.invoice_manager.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByRecipient(BusinessProfile recipient);
    List<Invoice> findByStatus(InvoiceStatus status);
    List<Invoice> findBySender(User sender);
    List<Invoice> findBySenderAndStatus(User sender, InvoiceStatus status);
}
