package com.wallstreet.stock.market.simulation.model;
import com.wallstreet.stock.market.simulation.model.enums.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "leaderboard")
public class LeaderboardEntry {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal cashBalance = BigDecimal.ZERO;

    @Column(precision = 18, scale = 6, nullable = false)
    private BigDecimal holdingsValue = BigDecimal.ZERO;

    @Column(precision = 18, scale = 6, nullable = false)
    private BigDecimal totalValue = BigDecimal.ZERO;

    private OffsetDateTime updatedAt;

    public LeaderboardEntry() { this.updatedAt = OffsetDateTime.now(); }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BigDecimal getCashBalance() { return cashBalance; }
    public void setCashBalance(BigDecimal cashBalance) { this.cashBalance = cashBalance; }

    public BigDecimal getHoldingsValue() { return holdingsValue; }
    public void setHoldingsValue(BigDecimal holdingsValue) { this.holdingsValue = holdingsValue; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
