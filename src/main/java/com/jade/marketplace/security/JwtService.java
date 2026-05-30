package com.jade.marketplace.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Responsible for creating and validating JWT tokens
 * JWT token payload contains:
 * - user email
 * - user roles
 * - issue timestamp
 * - expiration timestamp
 * 
 * Claims = payload data
 * JWT contains 3 things: HEADER.PAYLOAD.SIGNATURE
 */
@Service
public class JwtService {

    // get JWT secret key string from .env
    @Value("${jwt.secret}")
    private String jwtSecret;

    // get JWT token expiration time from .env
    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    /**
     * Creates the secret key used to sign and verify JWT tokens
     * The same key is used for:
     * - token creation
     * - token validation
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a JWT token for an authenticated user
     * Stores:
     * - email as subject
     * - user roles as custom claim
     * - issued timestamp
     * - expiration timestamp
     */
    public String generateToken(String email, Collection<? extends GrantedAuthority> authorities) {
        // get user roles
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // create timestamp
        Date now = new Date();
        // create expiration timestamp
        Date expiration = new Date(now.getTime() + jwtExpirationMs);

        // build and return JWT token using email, roles, time stamp and expiration timestamp
        return Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiration)
                // creates signature using signing key
                .signWith(getSigningKey())
                .compact();
    }

    // extract email from the token
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // check if token stores the same email and token is not expired
    public boolean isTokenValid(String token, String email) {
        String tokenEmail = extractEmail(token);
        return tokenEmail.equals(email) && !isTokenExpired(token);
    }

    // check if token is expired: has expiration before today's date
    private boolean isTokenExpired(String token) {
        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }

    // a JWT token extract function using signing key
    private Claims extractClaims(String token) {
        return Jwts.parser()
                // tell JWT library which signing key to use
                .verifyWith(getSigningKey())
                .build()
                // verify token
                .parseSignedClaims(token)
                // return claims/payload
                .getPayload();
    }
}