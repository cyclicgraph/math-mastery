package com.cyclicgraph.masterymath.config.security;

import com.cyclicgraph.masterymath.auth.model.Jwt;
import com.cyclicgraph.masterymath.auth.model.RefreshToken;
import com.cyclicgraph.masterymath.user.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.access.expiration.seconds}")
    private long accessExpiration;
    @Value("${jwt.refresh.expiration.seconds}")
    private long refreshExpiration;
    @Value("${jwt.secret-key}")
    private String secretKey;

    public Jwt generateAccessToken(String username) {
        if (StringUtils.isEmpty(username)) {
            throw new IllegalArgumentException("wrong username provided");
        }

        Date now = new Date();
        Date expiration = new Date(now.getTime() + (accessExpiration * 1000L));
        String token = Jwts.builder().subject(username)
                .issuedAt(now).expiration(expiration)
                .signWith(getSignInKey()).compact();

        return new Jwt(token, expiration);
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }


    public RefreshToken createRefreshToken(UserEntity user) {
        return new RefreshToken(null, System.currentTimeMillis() + (refreshExpiration * 1000), UUID.randomUUID(), user);
    }

    public boolean isTokenValid(String token) {
        try {
            String username = extractUsername(token);
            return username != null && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return (String) extractClaims(token).get("sub");
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            return new DefaultClaims(new HashMap<>());
        }
    }
}
