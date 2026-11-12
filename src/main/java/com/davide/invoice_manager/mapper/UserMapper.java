package com.davide.invoice_manager.mapper;

import com.davide.invoice_manager.command.UserRegistrationCommand;
import com.davide.invoice_manager.domain.User;
import com.davide.invoice_manager.dto.request.UserRegistrationRequest;
import com.davide.invoice_manager.dto.response.UserRegistrationResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserRegistrationCommand toCommand(UserRegistrationRequest request) {
        return new UserRegistrationCommand(
                request.username(),
                request.password()
        );
    }

    public UserRegistrationResponse toResponse(User user) {
        return new UserRegistrationResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
