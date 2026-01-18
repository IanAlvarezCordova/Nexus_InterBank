package com.ecusol.web.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key = Keys.hmacShaKeyFor(
            "MiClaveSecretaSuperLarga2025EcuSolBank1234567890abcdef".getBytes());

    @Value("${jwt.expiration-ms}")
    private long validityInMilliseconds;

    public String createToken(String username, Integer usuarioWebId, Integer clienteIdCore) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(username)
                .claim("id", clienteIdCore)          
                .claim("usuarioWebId", usuarioWebId) 
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) { return false; }
    }

    public Long getId(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return claims.get("id", Long.class);
    }

    public Integer getUsuarioWebId(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return claims.get("usuarioWebId", Integer.class);
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }
}

