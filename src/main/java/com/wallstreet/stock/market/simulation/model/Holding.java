package com.wallstreet.stock.market.simulation.model;
import com.wallstreet.stock.market.simulation.model.enums.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "holdings", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "symbol"})})
public class Holding {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "symbol")
    private Stock stock;

    @Column(nullable = false)
    private Long qty = 0L;

    @Column(nullable = false, precision = 18, scale = 6)
    private BigDecimal avgPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    private Long reservedQty = 0L;

    private OffsetDateTime updatedAt;

    public Holding() { this.id = UUID.randomUUID(); this.updatedAt = OffsetDateTime.now(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }

    public Long getQty() { return qty; }
    public void setQty(Long qty) { this.qty = qty; }

    public BigDecimal getAvgPrice() { return avgPrice; }
    public void setAvgPrice(BigDecimal avgPrice) { this.avgPrice = avgPrice; }

    public Long getReservedQty() { return reservedQty; }
    public void setReservedQty(Long reservedQty) { this.reservedQty = reservedQty; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
