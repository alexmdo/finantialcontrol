package br.com.alexmdo.finantialcontrol.user;

import br.com.alexmdo.finantialcontrol.domain.user.User;
import br.com.alexmdo.finantialcontrol.domain.user.UserRepository;
import br.com.alexmdo.finantialcontrol.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");

        // Mock the repository to return false (email does not exist)
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        // Mock the repository to return the saved user
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User createdUser = userService.createUserAsync(user).join();

        // Assert
        assertNotNull(createdUser);
        assertEquals(user.getEmail(), createdUser.getEmail());
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, times(1)).save(user);
    }


    @Test
    void testCreateUser_EmailAlreadyExists() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");

        // Mock the repository to return true (email already exists)
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(CompletionException.class, () -> userService.createUserAsync(user).join());

        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, never()).save(user);
    }

    @Test
    void testUpdateUser_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        // Mock the repository to return the user reference
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);

        // Mock the repository to return false (email does not exist)
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        // Mock the repository to return the saved user
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User updatedUser = userService.updateUserAsync(user).join();

        // Assert
        assertNotNull(updatedUser);
        assertEquals(user.getEmail(), updatedUser.getEmail());
        verify(userRepository, times(1)).getReferenceById(user.getId());
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUser_EmailAlreadyExists() {
        // Arrange
        var userId = 1L;
        var user = new User(userId, "John", "Doe", "test@example.com", "123456");
        var userToUpdate = new User(userId, "John", "Doe", "test@emailchanged.com", "123456");

        // Mock the repository to return the user reference
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);

        // Mock the repository to return true (email already exists)
        when(userRepository.existsByEmail(userToUpdate.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(CompletionException.class, () -> userService.updateUserAsync(userToUpdate).join());

        verify(userRepository, times(1)).getReferenceById(user.getId());
        verify(userRepository, times(1)).existsByEmail(userToUpdate.getEmail());
        verify(userRepository, never()).save(user);
    }

    @Test
    void testDeleteUser() {
        var userId = 1L;
        var user = new User(userId, "John", "Doe", "john@doe.com", "123456");

        userService.deleteUserAsync(userId, user).join();

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testGetUserById() {
        var userId = 1L;
        var user = new User(userId, "John", "Doe", "john@doe.com", "123456");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        var retrievedUser = userService.getUserByIdAndUserAsync(userId, user).join();

        verify(userRepository, times(1)).findById(userId);
        assertEquals(user, retrievedUser);
    }

    @Test
    void testGetUserByEmail() {
        var email = "johndoe@example.com";
        var user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        var retrievedUser = userService.getUserByEmailAsync(email).join();

        verify(userRepository, times(1)).findByEmail(email);
        assertEquals(user, retrievedUser);
    }

    @Test
    void testGetAllUsers() {
        var userList = new ArrayList<User>();
        userList.add(new User());
        userList.add(new User());

        var pageable = mock(Pageable.class);
        var userPage = new PageImpl<>(userList, pageable, userList.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        var retrievedUsers = userService.getAllUsersAsync(pageable).join();

        verify(userRepository, times(1)).findAll(pageable);
        assertEquals(userPage, retrievedUsers);
    }

}
