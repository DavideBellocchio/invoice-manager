package com.davide.invoice_manager.command;

public record UserRegistrationCommand(
        String username,
        String password
) {
}
