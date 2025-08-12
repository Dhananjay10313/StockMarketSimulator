package com.wallstreet.stock.market.simulation.service;

import com.wallstreet.stock.market.simulation.model.RefreshToken;
import com.wallstreet.stock.market.simulation.model.User;
import com.wallstreet.stock.market.simulation.repository.RefreshTokenRepository;
import com.wallstreet.stock.market.simulation.security.JwtProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service responsible for creating, validating, rotating and revoking refresh tokens.
 *
 * Behavior notes:
 *  - Single-session mode: when creating a new token for a user, all other active tokens
 *    for that user will be revoked (so only one refresh token is active per user).
 *  - Rotation: when rotating, a new token is issued and the old token is revoked with
 *    replacedByToken pointing to the new token string.
 */
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    /**
     * Create a new refresh token for the given user. Because we are in single-session mode,
     * this will revoke any previously active refresh tokens for the user.
     *
     * @param user user entity
     * @param ipAddress optional client IP
     * @param userAgent optional user agent string
     * @return persisted RefreshToken
     */
    @Transactional
    public RefreshToken createRefreshToken(User user, String ipAddress, String userAgent) {
        String newToken = generateTokenString();
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expiresAt = now.plusDays(jwtProperties.getRefreshTokenExpirationDays());

        // Revoke existing tokens (single-session). Mark replaced_by_token to the new token.
        revokeActiveTokensForUser(user.getId(), newToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(newToken);
        refreshToken.setCreatedAt(now);
        refreshToken.setExpiresAt(expiresAt);
        refreshToken.setRevoked(false);
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setUserAgent(userAgent);

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Validate the token string: present, not revoked, not expired.
     *
     * @param token token string
     * @return Optional of RefreshToken if valid, else Optional.empty()
     */
    @Transactional(readOnly = true)
    public Optional<RefreshToken> validateToken(String token) {
        if (token == null || token.isBlank()) return Optional.empty();

        Optional<RefreshToken> maybe = refreshTokenRepository.findByToken(token);
        if (maybe.isEmpty()) return Optional.empty();

        RefreshToken rt = maybe.get();
        if (rt.isRevoked()) return Optional.empty();
        if (rt.getExpiresAt() == null || rt.getExpiresAt().isBefore(OffsetDateTime.now())) return Optional.empty();

        return Optional.of(rt);
    }

    /**
     * Rotate an existing refresh token: revoke the old token and issue a new one for the same user.
     * This helps mitigate reuse attacks.
     *
     * Because single-session mode is enabled, this will also ensure no other active tokens remain.
     *
     * @param oldToken existing RefreshToken entity (must be present and not revoked)
     * @param ipAddress optional client IP for new token
     * @param userAgent optional user agent for new token
     * @return newly created RefreshToken
     */
    @Transactional
    public RefreshToken rotate(RefreshToken oldToken, String ipAddress, String userAgent) {
        // Defensive checks
        if (oldToken == null) throw new IllegalArgumentException("oldToken must not be null");
        if (oldToken.isRevoked()) throw new IllegalStateException("Cannot rotate a revoked token");

        String newTokenString = generateTokenString();
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime newExpiry = now.plusDays(jwtProperties.getRefreshTokenExpirationDays());

        // Revoke all active tokens for user and mark them replaced by the new token (single-session)
        revokeActiveTokensForUser(oldToken.getUser().getId(), newTokenString);

        // Mark old token revoked and link to new token
        oldToken.setRevoked(true);
        oldToken.setRevokedAt(now);
        oldToken.setReplacedByToken(newTokenString);
        refreshTokenRepository.save(oldToken);

        // Create new RefreshToken
        RefreshToken newRt = new RefreshToken();
        newRt.setUser(oldToken.getUser());
        newRt.setToken(newTokenString);
        newRt.setCreatedAt(now);
        newRt.setExpiresAt(newExpiry);
        newRt.setRevoked(false);
        newRt.setIpAddress(ipAddress);
        newRt.setUserAgent(userAgent);

        return refreshTokenRepository.save(newRt);
    }

    /**
     * Revoke a specific refresh token.
     */
    @Transactional
    public void revokeToken(RefreshToken token) {
        if (token == null) return;
        token.setRevoked(true);
        token.setRevokedAt(OffsetDateTime.now());
        refreshTokenRepository.save(token);
    }

    /**
     * Revoke all tokens for a user. If 'replacedBy' is non-null, set replaced_by_token on existing tokens.
     */
    @Transactional
    public void revokeActiveTokensForUser(UUID userId, String replacedBy) {
        List<RefreshToken> active = refreshTokenRepository.findByUser_IdAndRevokedFalse(userId);
        OffsetDateTime now = OffsetDateTime.now();
        for (RefreshToken t : active) {
            t.setRevoked(true);
            t.setRevokedAt(now);
            if (replacedBy != null) t.setReplacedByToken(replacedBy);
        }
        if (!active.isEmpty()) refreshTokenRepository.saveAll(active);
    }

    /**
     * Cleanup expired tokens (optional scheduled job can call this).
     */
    @Transactional
    public void removeExpiredTokens() {
        refreshTokenRepository.deleteByExpiresAtBefore(OffsetDateTime.now());
    }

    /**
     * Find by token raw string.
     */
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /* -------------------- internals -------------------- */

    private String generateTokenString() {
        // Combine UUID + secure random bytes Base64 for added entropy
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String rand = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return UUID.randomUUID().toString() + "-" + rand;
    }
}
