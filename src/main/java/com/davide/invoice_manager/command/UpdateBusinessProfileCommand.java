package com.davide.invoice_manager.command;


public record UpdateBusinessProfileCommand(
        String businessName,
        String vatCode,
        String pec,
        String phoneNumber
) {
}
