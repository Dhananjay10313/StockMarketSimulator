package com.wallstreet.stock.market.simulation.model;
import com.wallstreet.stock.market.simulation.model.enums.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;


@Entity
@Table(name = "users")
public class User {
    @Id
    // keep columnDefinition "uuid" for Postgres; constructor/PrePersist will assign a UUID client-side
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // maps to display_name
    @Column(name = "display_name")
    private String displayName;

    // maps to password_hash
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // maps to role, DB default is 'USER' but set default here too so JPA persists value if omitted
    @Column(name = "role", nullable = false)
    private String role = "USER";

    // maps to created_at (Postgres TIMESTAMP WITH TIME ZONE)
    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime createdAt;

    // maps to last_active_at (Postgres TIMESTAMP WITH TIME ZONE)
    @Column(name = "last_active_at", columnDefinition = "timestamp with time zone")
    private OffsetDateTime lastActiveAt;

    public User() {
        // keep these so existing code works; PrePersist will also ensure values if constructor isn't used as expected
        if (this.id == null) this.id = UUID.randomUUID();
        if (this.createdAt == null) this.createdAt = OffsetDateTime.now();
    }

    @PrePersist
    private void prePersist() {
        if (this.id == null) this.id = UUID.randomUUID();
        if (this.createdAt == null) this.createdAt = OffsetDateTime.now();
        if (this.role == null) this.role = "USER";
    }

    // getters & setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getLastActiveAt() { return lastActiveAt; }
    public void setLastActiveAt(OffsetDateTime lastActiveAt) { this.lastActiveAt = lastActiveAt; }
}

