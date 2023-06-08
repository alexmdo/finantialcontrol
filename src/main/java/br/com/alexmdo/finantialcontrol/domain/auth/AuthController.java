package br.com.alexmdo.finantialcontrol.domain.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alexmdo.finantialcontrol.domain.auth.dto.LoginRequestDto;
import br.com.alexmdo.finantialcontrol.domain.auth.dto.TokenJWTDto;
import br.com.alexmdo.finantialcontrol.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<TokenJWTDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequestDto.login(),
                loginRequestDto.password());
        var authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        var token = tokenService.generateToken((User) authenticate.getPrincipal());

        return ResponseEntity.ok(new TokenJWTDto(token));
    }

}
