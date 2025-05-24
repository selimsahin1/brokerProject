package com.selimsahin.broker.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String generateToken(String username, boolean isAdmin) {
        return Jwts.builder()
                .setSubject(username)
                .claim("isAdmin", isAdmin)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean extractIsAdmin(String token) {
        return Boolean.TRUE.equals(Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().get("isAdmin", Boolean.class));
    }
}
