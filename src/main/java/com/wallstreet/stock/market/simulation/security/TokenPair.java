package com.wallstreet.stock.market.simulation.security;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;

/**
 * Simple DTO to return access token metadata (and optionally refresh token metadata).
 * We typically return accessToken & accessTokenExpiresAt in the response body.
 * Refresh token is sent via HttpOnly cookie; refreshToken fields are optional and may be null.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenPair {

    private String accessToken;
    private OffsetDateTime accessTokenExpiresAt;

    private String refreshToken;
    private OffsetDateTime refreshTokenExpiresAt;

    public TokenPair() {}

    public TokenPair(String accessToken, OffsetDateTime accessTokenExpiresAt,
                     String refreshToken, OffsetDateTime refreshTokenExpiresAt) {
        this.accessToken = accessToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public OffsetDateTime getAccessTokenExpiresAt() {
        return accessTokenExpiresAt;
    }

    public void setAccessTokenExpiresAt(OffsetDateTime accessTokenExpiresAt) {
        this.accessTokenExpiresAt = accessTokenExpiresAt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public OffsetDateTime getRefreshTokenExpiresAt() {
        return refreshTokenExpiresAt;
    }

    public void setRefreshTokenExpiresAt(OffsetDateTime refreshTokenExpiresAt) {
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }
}
