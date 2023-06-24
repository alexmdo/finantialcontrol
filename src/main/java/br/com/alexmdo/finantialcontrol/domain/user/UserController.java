package br.com.alexmdo.finantialcontrol.domain.user;

import br.com.alexmdo.finantialcontrol.domain.user.dto.UserCreateRequestDto;
import br.com.alexmdo.finantialcontrol.domain.user.dto.UserDto;
import br.com.alexmdo.finantialcontrol.domain.user.dto.UserUpdateRequestDto;
import br.com.alexmdo.finantialcontrol.infra.BaseController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class UserController extends BaseController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public CompletableFuture<ResponseEntity<UserDto>> createUser(@Valid @RequestBody UserCreateRequestDto createRequestDto) {
        var user = userMapper.toEntity(createRequestDto);
        return userService.createUser(user)
                .thenApply(createdUser -> {
                    var responseDto = userMapper.toDto(createdUser);
                    return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
                });
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<UserDto>> updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserUpdateRequestDto updateRequestDto) {
        var user = super.getPrincipal();
        return userService.getUserByIdAndUser(id, user)
                .thenCompose(existingUser -> {
                    var updatedUser = userMapper.updateEntity(existingUser, updateRequestDto);
                    return userService.updateUser(updatedUser);
                })
                .thenApply(savedUser -> {
                    var responseDto = userMapper.toDto(savedUser);
                    return ResponseEntity.ok(responseDto);
                });
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteUser(@PathVariable("id") Long id) {
        var user = super.getPrincipal();
        return userService.deleteUser(id, user)
                .thenApply(ignored -> ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<UserDto>> getUserById(@PathVariable("id") Long id) {
        var principal = super.getPrincipal();
        return userService.getUserByIdAndUser(id, principal)
                .thenApply(user -> {
                    var responseDto = userMapper.toDto(user);
                    return ResponseEntity.ok(responseDto);
                });
    }

}
