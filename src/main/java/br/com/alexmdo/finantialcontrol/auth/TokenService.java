package br.com.alexmdo.finantialcontrol.auth;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import br.com.alexmdo.finantialcontrol.user.User;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(User principal) {
        var algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("Finantial Control")
                .withSubject(principal.getUsername())
                .withClaim("id", principal.getId())
                .withExpiresAt(expireAt())
                .sign(algorithm);
    }

    private Instant expireAt() {
        return LocalDateTime.now().plusMinutes(15).atZone(ZoneId.systemDefault()).toInstant();
    }

    public String getSubject(String token) {
        var algorithm = Algorithm.HMAC256(secret);
        return JWT.require(algorithm)
                .withIssuer("Finantial Control")
                .build()
                .verify(token)
                .getSubject();
    }

}
