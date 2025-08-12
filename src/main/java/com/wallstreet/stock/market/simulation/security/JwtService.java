package com.wallstreet.stock.market.simulation.security;

import com.wallstreet.stock.market.simulation.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

/**
 * Utility service for creating and validating access JWTs.
 *
 * This service issues signed HS256 tokens containing:
 *  - subject = user's email
 *  - claims: userId, roles (single or comma-separated)
 *
 * The service intentionally only handles access JWTs. Refresh tokens should be opaque
 * values persisted via RefreshTokenRepository (see earlier).
 */
@Service
public class JwtService {

    private final Key signingKey;
    private final long accessTokenExpiryMinutes;

    public JwtService(JwtProperties props) {
        if (props.getSecret() == null || props.getSecret().length() < 32) {
            throw new IllegalArgumentException("JWT secret must be set and at least 32 characters long");
        }
        // use raw bytes of secret for HMAC key (ensure enough entropy)
        this.signingKey = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiryMinutes = props.getAccessTokenExpirationMinutes();
    }

    /**
     * Generate a signed access JWT for the given user.
     *
     * @param user application user entity (must contain getEmail(), getId(), getRole())
     * @return JWT compact string
     */
    public String generateAccessToken(User user) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusMinutes(accessTokenExpiryMinutes);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", Optional.ofNullable(user.getId()).map(Object::toString).orElse(null))
                .claim("roles", user.getRole()) // if multiple roles later, put comma-separated or list
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expiry.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate the token signature & expiry.
     *
     * @param token JWT string
     * @return true if token is valid (signature + not expired)
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // includes expired, malformed, unsupported, signature exceptions
            return false;
        }
    }

    /**
     * Extract subject (email) from token.
     */
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    /**
     * Extract a custom claim (userId) as String.
     */
    public String extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        Object id = claims.get("userId");
        return id != null ? id.toString() : null;
    }

    /**
     * Extract roles claim (as String).
     */
    public String extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        Object roles = claims.get("roles");
        return roles != null ? roles.toString() : null;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
