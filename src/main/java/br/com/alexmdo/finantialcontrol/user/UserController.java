package br.com.alexmdo.finantialcontrol.user;

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

import br.com.alexmdo.finantialcontrol.infra.BaseController;
import br.com.alexmdo.finantialcontrol.user.dto.UserCreateRequestDto;
import br.com.alexmdo.finantialcontrol.user.dto.UserDto;
import br.com.alexmdo.finantialcontrol.user.dto.UserUpdateRequestDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class UserController extends BaseController {

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
        var user = super.getPrincipal();
        var existingUser = userService.getUserByIdAndUser(id, user);
        var updatedUser = userMapper.updateEntity(existingUser, updateRequestDto);
        var savedUser = userService.updateUser(updatedUser);
        var responseDto = userMapper.toDto(savedUser);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        var user = super.getPrincipal();
        userService.deleteUser(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long id) {
        var principal = super.getPrincipal();
        var user = userService.getUserByIdAndUser(id, principal);
        var responseDto = userMapper.toDto(user);
        return ResponseEntity.ok(responseDto);
    }

}
