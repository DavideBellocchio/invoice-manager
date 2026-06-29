package com.davide.invoice_manager.repository;

import com.davide.invoice_manager.domain.Invoice;
import com.davide.invoice_manager.domain.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

    List<InvoiceItem> findByInvoice(Invoice invoice);
}
