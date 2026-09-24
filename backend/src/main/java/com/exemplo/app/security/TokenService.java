package com.exemplo.app.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.exemplo.app.model.Usuario;

@Service
public class TokenService {

    private static final String ISSUER = "sistema-de-matriculas";

    private final Algorithm algorithm;
    private final Duration expiracao;

    public TokenService(@Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiracao-minutos}") long expiracaoMinutos) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expiracao = Duration.ofMinutes(expiracaoMinutos);
    }

    public String gerar(Usuario usuario) {
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(usuario.getEmail())
                .withClaim("perfil", usuario.getPerfil())
                .withExpiresAt(Instant.now().plus(expiracao))
                .sign(algorithm);
    }

    public Optional<String> validar(String token) {
        try {
            return Optional.of(JWT.require(algorithm).withIssuer(ISSUER).build().verify(token).getSubject());
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }
}
