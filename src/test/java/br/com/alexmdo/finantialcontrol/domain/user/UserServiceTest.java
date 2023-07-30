package br.com.alexmdo.finantialcontrol.domain.user;

import br.com.alexmdo.finantialcontrol.domain.user.exception.UserAlreadyRegisteredException;
import br.com.alexmdo.finantialcontrol.domain.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUserAsync_Success() {
        // Given
        User user = new User(null, "John", "Doe", "john.doe@example.com", "password");

        Mockito.when(userRepository.existsByEmail(anyString())).thenReturn(false);
        Mockito.when(userRepository.save(any())).thenReturn(user);

        // When
        CompletableFuture<User> futureUser = userService.createUserAsync(user);

        // Then
        assertDoesNotThrow(futureUser::join);
        User createdUser = futureUser.join();
        assertNotNull(createdUser);
        assertEquals("John", createdUser.getFirstName());
        assertEquals("Doe", createdUser.getLastName());
        assertEquals("john.doe@example.com", createdUser.getEmail());
        assertEquals("password", createdUser.getPassword());
    }

    @Test
    public void testCreateUserAsync_UserAlreadyRegisteredException() {
        // Given
        User newUser = new User(null, "John", "Doe", "john.doe@example.com", "password");

        Mockito.when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When
        CompletableFuture<User> futureUser = userService.createUserAsync(newUser);

        // Then
        CompletionException completionException = assertThrows(CompletionException.class, futureUser::join);
        assertTrue(completionException.getCause() instanceof UserAlreadyRegisteredException);
        assertEquals("Email already exists", completionException.getCause().getMessage());
    }


    @Test
    public void testUpdateUserAsync_Success() {
        // Given
        Long userId = 1L;
        User existingUser = new User(userId, "John", "Doe", "john.doe@example.com", "password");
        User updatedUser = new User(userId, "John", "Updated", "john.updated@example.com", "newPassword");

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty()); // Assume the updated email doesn't exist
        Mockito.when(userRepository.save(any())).thenReturn(updatedUser);

        // When
        CompletableFuture<User> futureUser = userService.updateUserAsync(updatedUser);

        // Then
        assertDoesNotThrow(futureUser::join);
        User updated = futureUser.join();
        assertNotNull(updated);
        assertEquals("John", updated.getFirstName());
        assertEquals("Updated", updated.getLastName());
        assertEquals("john.updated@example.com", updated.getEmail());
        assertEquals("newPassword", updated.getPassword());
    }


    @Test
    public void testUpdateUserAsync_UserNotFoundException() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        // Mock the userRepository.findById method to return an empty optional, simulating user not found
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        CompletableFuture<User> futureUpdate = userService.updateUserAsync(user);

        // Then
        CompletionException completionException = assertThrows(CompletionException.class, () -> futureUpdate.join());
        Throwable cause = completionException.getCause();
        assertTrue(cause instanceof UserNotFoundException);
        assertEquals("User not found given the id", cause.getMessage());
    }


    @Test
    public void testDeleteUserAsync_Success() {
        // Given
        Long userId = 1L;
        User user = new User(userId, "John", "Doe", "john.doe@example.com", "password");

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // When
        CompletableFuture<Void> futureVoid = userService.deleteUserAsync(userId, user);

        // Then
        assertDoesNotThrow(futureVoid::join);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);
    }

    @Test
    public void testDeleteUserAsync_UserNotFoundException() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(2L);

        // Mock the userRepository.findById method to return an empty optional, simulating user not found
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        CompletableFuture<Void> futureDelete = userService.deleteUserAsync(userId, user);

        // Then
        CompletionException completionException = assertThrows(CompletionException.class, futureDelete::join);
        Throwable cause = completionException.getCause();
        assertTrue(cause instanceof UserNotFoundException);
        assertEquals("User not found given the id", cause.getMessage());
    }


    @Test
    public void testGetUserByIdAndUserAsync_Success() {
        // Given
        Long userId = 1L;
        User user = new User(userId, "John", "Doe", "john.doe@example.com", "password");

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // When
        CompletableFuture<User> futureUser = userService.getUserByIdAndUserAsync(userId, user);

        // Then
        assertDoesNotThrow(futureUser::join);
        User foundUser = futureUser.join();
        assertNotNull(foundUser);
        assertEquals("John", foundUser.getFirstName());
        assertEquals("Doe", foundUser.getLastName());
        assertEquals("john.doe@example.com", foundUser.getEmail());
        assertEquals("password", foundUser.getPassword());
    }

    @Test
    public void testGetUserByIdAndUserAsync_UserNotFoundException() {
        // Given
        Long userId = 1L;
        User user = new User(userId, "John", "Doe", "john.doe@example.com", "password");

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        CompletableFuture<User> futureUser = userService.getUserByIdAndUserAsync(userId, user);

        // Then
        CompletionException completionException = assertThrows(CompletionException.class, futureUser::join);
        assertTrue(completionException.getCause() instanceof UserNotFoundException);
        assertEquals("User not found given the id", completionException.getCause().getMessage());
    }


    @Test
    public void testGetUserByEmailAsync_Success() {
        // Given
        String email = "john.doe@example.com";
        User user = new User(1L, "John", "Doe", "john.doe@example.com", "password");

        Mockito.when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        // When
        CompletableFuture<User> futureUser = userService.getUserByEmailAsync(email);

        // Then
        assertDoesNotThrow(futureUser::join);
        User foundUser = futureUser.join();
        assertNotNull(foundUser);
        assertEquals("John", foundUser.getFirstName());
        assertEquals("Doe", foundUser.getLastName());
        assertEquals("john.doe@example.com", foundUser.getEmail());
        assertEquals("password", foundUser.getPassword());
    }

    @Test
    public void testGetUserByEmailAsync_UserNotFoundException() {
        // Given
        String userEmail = "john.doe@example.com";

        Mockito.when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When
        CompletableFuture<User> futureUser = userService.getUserByEmailAsync(userEmail);

        // Then
        CompletionException completionException = assertThrows(CompletionException.class, futureUser::join);
        assertTrue(completionException.getCause() instanceof UserNotFoundException);
        assertEquals("User not found given the email", completionException.getCause().getMessage());
    }

}
