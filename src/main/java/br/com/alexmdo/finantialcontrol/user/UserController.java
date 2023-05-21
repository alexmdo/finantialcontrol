package br.com.alexmdo.finantialcontrol.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alexmdo.finantialcontrol.user.dto.UserCreateRequestDto;
import br.com.alexmdo.finantialcontrol.user.dto.UserDto;
import br.com.alexmdo.finantialcontrol.user.dto.UserMapper;
import br.com.alexmdo.finantialcontrol.user.dto.UserUpdateRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserCreateRequestDto createRequestDto) {
        var user = userMapper.toEntity(createRequestDto);
        var createdUser = userService.createUser(user);
        var responseDto = userMapper.toDto(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserUpdateRequestDto updateRequestDto) {
        var existingUser = userService.getUserById(id);
        var updatedUser = userMapper.updateEntity(existingUser, updateRequestDto);
        var savedUser = userService.updateUser(updatedUser);
        var responseDto = userMapper.toDto(savedUser);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> getUsers(Pageable pageable) {
        var userPage = userService.getAllUsers(pageable);
        var responsePage = userPage.map(userMapper::toDto);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long id) {
        var user = userService.getUserById(id);
        var responseDto = userMapper.toDto(user);
        return ResponseEntity.ok(responseDto);
    }

}
