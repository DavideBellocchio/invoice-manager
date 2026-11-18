package com.davide.invoice_manager.service;

import com.davide.invoice_manager.command.UserRegistrationCommand;
import com.davide.invoice_manager.domain.Role;
import com.davide.invoice_manager.domain.User;
import com.davide.invoice_manager.exception.ConflictException;
import com.davide.invoice_manager.exception.ResourceNotFoundException;
import com.davide.invoice_manager.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private String unknowUsername;
    private Long unknowUserId;
    private UserRegistrationCommand userRegistrationCommand;
    private UserRegistrationCommand userRegistrationCommandNotNormalized;
    private String encodedPwd = "hashed-password";

    @BeforeEach
    public void init() {
        testUser = new User(
                1L,
                "admin@gmail.com",
                "password",
                Role.ADMIN,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        unknowUsername = "nessuno";
        unknowUserId = 999L;

        userRegistrationCommand = new UserRegistrationCommand(
                "admin@gmail.com",
                "Plain-Password-123"
        );
        userRegistrationCommandNotNormalized = new UserRegistrationCommand(
                " Prova@gmail.com ",
                "Plain-Password-123"
        );
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

    @Test
    public void shouldRegisterUserWithEncodedPasswordAndUserRole_whenUsernameIsAvailable(){
        Mockito.when(userRepository.existsByUsername(userRegistrationCommand.username())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(userRegistrationCommand.password())).thenReturn(encodedPwd);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(testUser);
        User u = userService.register(userRegistrationCommand);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        Assertions.assertSame(testUser, u);
        Assertions.assertEquals("admin@gmail.com", savedUser.getUsername());
        Assertions.assertEquals(encodedPwd, savedUser.getPassword());
        Assertions.assertSame(Role.USER, savedUser.getRole());
    }

    @Test
    public void shouldThrowConflictException_whenUsernameAlreadyExists(){
        Mockito.when(userRepository.existsByUsername(userRegistrationCommand.username())).thenReturn(true);
        Assertions.assertThrows(ConflictException.class, () -> userService.register(userRegistrationCommand));
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.any());
    }

    @Test
    public void shouldSaveNormalizedUsername_whenUsernameHasUppercaseAndSpaces(){
        Mockito.when(userRepository.existsByUsername("prova@gmail.com")).thenReturn(false);
        User u = userService.register(userRegistrationCommandNotNormalized);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        Assertions.assertEquals("prova@gmail.com", savedUser.getUsername());
    }
}
