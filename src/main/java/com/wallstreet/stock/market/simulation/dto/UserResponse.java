package com.wallstreet.stock.market.simulation.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UserResponse {
    private UUID id;
    private String email;
    private String displayName;
    private String role;
    private OffsetDateTime createdAt;
    private OffsetDateTime lastActiveAt;

    public UserResponse(UUID id, String email, String displayName, String role,
                        OffsetDateTime createdAt, OffsetDateTime lastActiveAt) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
        this.createdAt = createdAt;
        this.lastActiveAt = lastActiveAt;
    }

    // getters only (immutable response)
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public String getRole() { return role; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getLastActiveAt() { return lastActiveAt; }
}
