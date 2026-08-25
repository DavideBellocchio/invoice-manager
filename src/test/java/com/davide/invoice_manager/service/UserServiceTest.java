package com.davide.invoice_manager.service;

import com.davide.invoice_manager.domain.Product;
import com.davide.invoice_manager.domain.Role;
import com.davide.invoice_manager.domain.User;
import com.davide.invoice_manager.exception.ResourceNotFoundException;
import com.davide.invoice_manager.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private String unknowUsername;
    private Long unknowUserId;

    @BeforeEach
    public void init() {
        testUser = new User(
                1L,
                "admin",
                "password",
                Role.ADMIN,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        unknowUsername = "nessuno";
        unknowUserId = 999L;
    }

    @Test
    public void getUserByUsername_shouldThrowException_whenUsernameNotFound() {
        Mockito.when(userRepository.findByUsername(unknowUsername)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () ->  userService.getUserByUsername(unknowUsername));
    }

    @Test
    public void getUserByUsername_shouldReturnUser_whenUsernameExist() {
        Mockito.when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        User result = userService.getUserByUsername(testUser.getUsername());
        Assertions.assertSame(testUser, result);
    }

    @Test
    public void getUserById_shouldThrowException_whenUserIdNotFound(){
        Mockito.when(userRepository.findById(unknowUserId)).thenReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class, () ->  userService.getUserById(unknowUserId));
    }

    @Test
    public void getUserById_shouldReturnUser_whenUserIdExist() {
        Mockito.when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        User result = userService.getUserById(testUser.getId());
        Assertions.assertSame(testUser, result);
    }
}
