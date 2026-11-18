package com.davide.invoice_manager.service;

import com.davide.invoice_manager.command.UserRegistrationCommand;
import com.davide.invoice_manager.domain.Role;
import com.davide.invoice_manager.domain.User;
import com.davide.invoice_manager.exception.ConflictException;
import com.davide.invoice_manager.exception.ResourceNotFoundException;
import com.davide.invoice_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserByUsername(String username) {
        String usernameNormalized = normalizeUsername(username);
        return userRepository.findByUsername(usernameNormalized)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User register(UserRegistrationCommand command) {
        String username = normalizeUsername(command.username());
        if(userRepository.existsByUsername(username)){
            throw new ConflictException("Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(command.password()));
        user.setRole(Role.USER);
        return userRepository.save(user);

    }

    private String normalizeUsername(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }
}
