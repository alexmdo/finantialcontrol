package br.com.alexmdo.finantialcontrol.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import br.com.alexmdo.finantialcontrol.user.dto.UserCreateRequestDto;
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
        var userId = 1L;
        var createRequestDto = new UserCreateRequestDto("John", "Doe", "johndoe@example.com");
        var expectedUser = new User(userId, "John", "Doe", "johndoe@example.com");

        when(userService.createUser(any())).thenReturn(expectedUser);

        var response = userController.createUser(createRequestDto);

        verify(userService, times(1)).createUser(any());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedUser.getId(), response.getBody().id());
        assertEquals(expectedUser.getFirstName(), response.getBody().firstName());
        assertEquals(expectedUser.getLastName(), response.getBody().lastName());
        assertEquals(expectedUser.getEmail(), response.getBody().email());
    }

    @Test
    void testUpdateUser() {
        var userId = 1L;
        var updateRequest = new UserUpdateRequestDto("John", "Doe", "johndoe@example.com");
        var expectedUser = new User(userId, "John", "Doe", "johndoe@example.com");

        when(userService.updateUser(any())).thenReturn(expectedUser);

        var response = userController.updateUser(userId, updateRequest);

        verify(userService, times(1)).updateUser(any());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUser.getId(), response.getBody().id());
        assertEquals(expectedUser.getFirstName(), response.getBody().firstName());
        assertEquals(expectedUser.getLastName(), response.getBody().lastName());
        assertEquals(expectedUser.getEmail(), response.getBody().email());
    }

    @Test
    void testDeleteUser() {
        var userId = 1L;

        var response = userController.deleteUser(userId);

        verify(userService, times(1)).deleteUser(userId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGetUserById() {
        var userId = 1L;
        var expectedUser = new User(userId, "John", "Doe", "johndoe@example.com");

        when(userService.getUserById(userId)).thenReturn(expectedUser);

        var response = userController.getUserById(userId);

        verify(userService, times(1)).getUserById(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUser.getId(), response.getBody().id());
        assertEquals(expectedUser.getFirstName(), response.getBody().firstName());
        assertEquals(expectedUser.getLastName(), response.getBody().lastName());
        assertEquals(expectedUser.getEmail(), response.getBody().email());
    }

    @Test
    void testGetAllUsers() {
        var expectedUserList = Arrays.asList(
                new User(1L, "John", "Doe", "johndoe@example.com"),
                new User(2L, "Jane", "Smith", "janesmith@example.com"));

        var pageable = mock(Pageable.class);
        var userPage = new PageImpl<>(expectedUserList, pageable, expectedUserList.size());

        when(userService.getAllUsers(any())).thenReturn(userPage);

        var response = userController.getUsers(pageable);

        verify(userService, times(1)).getAllUsers(any());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUserList.size(), response.getBody().getSize());
        for (int i = 0; i < expectedUserList.size(); i++) {
            var expectedUser = expectedUserList.get(i);
            var userDto = response.getBody().getContent().get(i);
            assertEquals(expectedUser.getId(), userDto.id());
            assertEquals(expectedUser.getFirstName(), userDto.firstName());
            assertEquals(expectedUser.getLastName(), userDto.lastName());
            assertEquals(expectedUser.getEmail(), userDto.email());
        }
    }
}
