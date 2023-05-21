package br.com.alexmdo.finantialcontrol.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
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
    void testCreateUser() {
        var user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");

        when(userRepository.save(user)).thenReturn(user);

        var createdUser = userService.createUser(user);

        verify(userRepository, times(1)).save(user);
        assertEquals(user, createdUser);
    }

    @Test
    void testUpdateUser() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("johndoe@example.com");

        when(userRepository.save(user)).thenReturn(user);

        var updatedUser = userService.updateUser(user);

        verify(userRepository, times(1)).save(user);
        assertEquals(user, updatedUser);
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
