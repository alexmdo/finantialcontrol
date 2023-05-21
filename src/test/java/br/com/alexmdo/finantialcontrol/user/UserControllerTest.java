package br.com.alexmdo.finantialcontrol.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.alexmdo.finantialcontrol.user.dto.UserCreateRequestDto;
import br.com.alexmdo.finantialcontrol.user.dto.UserDto;
import br.com.alexmdo.finantialcontrol.user.dto.UserUpdateRequestDto;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        Long userId = 1L;
        UserCreateRequestDto createRequestDto = new UserCreateRequestDto("John", "Doe", "johndoe@example.com");
        User expectedUser = new User(userId, "John", "Doe", "johndoe@example.com");

        when(userService.createUser(any(User.class))).thenReturn(expectedUser);

        ResponseEntity<UserDto> response = userController.createUser(createRequestDto);

        verify(userService, times(1)).createUser(any(User.class));
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedUser.getId(), response.getBody().id());
        assertEquals(expectedUser.getFirstName(), response.getBody().firstName());
        assertEquals(expectedUser.getLastName(), response.getBody().lastName());
        assertEquals(expectedUser.getEmail(), response.getBody().email());
    }

    @Test
    void testUpdateUser() {
        Long userId = 1L;
        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto("John", "Doe", "johndoe@example.com");
        User user = new User(userId, "John", "Doe", "johndoe@example.com");

        User expectedUser = new User(userId, "John", "Doe", "johndoe@example.com");

        when(userService.updateUser(user)).thenReturn(expectedUser);

        ResponseEntity<UserDto> response = userController.updateUser(userId, updateRequest);

        verify(userService, times(1)).updateUser(user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUser.getId(), response.getBody().id());
        assertEquals(expectedUser.getFirstName(), response.getBody().firstName());
        assertEquals(expectedUser.getLastName(), response.getBody().lastName());
        assertEquals(expectedUser.getEmail(), response.getBody().email());
    }

    @Test
    void testDeleteUser() {
        Long userId = 1L;

        ResponseEntity<Void> response = userController.deleteUser(userId);

        verify(userService, times(1)).deleteUser(userId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGetUserById() {
        Long userId = 1L;
        User expectedUser = new User(userId, "John", "Doe", "johndoe@example.com");

        when(userService.getUserById(userId)).thenReturn(expectedUser);

        ResponseEntity<UserDto> response = userController.getUserById(userId);

        verify(userService, times(1)).getUserById(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUser.getId(), response.getBody().id());
        assertEquals(expectedUser.getFirstName(), response.getBody().firstName());
        assertEquals(expectedUser.getLastName(), response.getBody().lastName());
        assertEquals(expectedUser.getEmail(), response.getBody().email());
    }

    @Test
    void testGetAllUsers() {
        List<User> expectedUserList = Arrays.asList(
                new User(1L, "John", "Doe", "johndoe@example.com"),
                new User(2L, "Jane", "Smith", "janesmith@example.com"));

        Pageable pageable = mock(Pageable.class);
        Page<User> userPage = new PageImpl<>(expectedUserList, pageable, expectedUserList.size());

        when(userService.getAllUsers(pageable)).thenReturn(userPage);

        ResponseEntity<Page<UserDto>> response = userController.getUsers(pageable);

        verify(userService, times(1)).getAllUsers(pageable);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserList.size(), response.getBody().getSize());
        for (int i = 0; i < expectedUserList.size(); i++) {
            User expectedUser = expectedUserList.get(i);
            UserDto userDto = response.getBody().getContent().get(i);
            assertEquals(expectedUser.getId(), userDto.id());
            assertEquals(expectedUser.getFirstName(), userDto.firstName());
            assertEquals(expectedUser.getLastName(), userDto.lastName());
            assertEquals(expectedUser.getEmail(), userDto.email());
        }
    }
}
