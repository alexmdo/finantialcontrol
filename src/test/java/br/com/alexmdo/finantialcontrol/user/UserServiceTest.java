package br.com.alexmdo.finantialcontrol.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import br.com.alexmdo.finantialcontrol.user.exception.UserAlreadyRegisteredException;

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
    public void testCreateUser_Success() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");

        // Mock the repository to return false (email does not exist)
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        // Mock the repository to return the saved user
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User createdUser = userService.createUser(user);

        // Assert
        assertNotNull(createdUser);
        assertEquals(user.getEmail(), createdUser.getEmail());
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testCreateUser_EmailAlreadyExists() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");

        // Mock the repository to return true (email already exists)
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyRegisteredException.class, () -> {
            userService.createUser(user);
        });

        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, never()).save(user);
    }

    @Test
    public void testUpdateUser_Success() {
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
        User updatedUser = userService.updateUser(user);

        // Assert
        assertNotNull(updatedUser);
        assertEquals(user.getEmail(), updatedUser.getEmail());
        verify(userRepository, times(1)).getReferenceById(user.getId());
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateUser_EmailAlreadyExists() {
        // Arrange
        var userId = 1L;
        var user = new User(userId, "John", "Doe", "test@example.com", "123456");
        var userToUpdate = new User(userId, "John", "Doe", "test@emailchanged.com", "123456");

        // Mock the repository to return the user reference
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);

        // Mock the repository to return true (email already exists)
        when(userRepository.existsByEmail(userToUpdate.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyRegisteredException.class, () -> {
            userService.updateUser(userToUpdate);
        });

        verify(userRepository, times(1)).getReferenceById(user.getId());
        verify(userRepository, times(1)).existsByEmail(userToUpdate.getEmail());
        verify(userRepository, never()).save(user);
    }

    @Test
    void testDeleteUser() {
        var userId = 1L;

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testGetUserById() {
        var userId = 1L;
        var user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        var retrievedUser = userService.getUserById(userId);

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

        var retrievedUser = userService.getUserByEmail(email);

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

        var retrievedUsers = userService.getAllUsers(pageable);

        verify(userRepository, times(1)).findAll(pageable);
        assertEquals(userPage, retrievedUsers);
    }
}
