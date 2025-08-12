package com.wallstreet.stock.market.simulation.model;
import com.wallstreet.stock.market.simulation.model.enums.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "gtt_orders", indexes = {
        @Index(name = "idx_gtt_symbol_trigger", columnList = "symbol, trigger_type, trigger_price"),
        @Index(name = "idx_gtt_user", columnList = "user_id")
})
public class GttOrder {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "symbol")
    private Stock stock;

    @Enumerated(EnumType.STRING)
    private OrderSide side;

    @Enumerated(EnumType.STRING)
    private OrderType type = OrderType.LIMIT;

    @Enumerated(EnumType.STRING)
    private GttTriggerType triggerType;

    @Column(precision = 18, scale = 6, nullable = false)
    private BigDecimal triggerPrice;

    @Column(precision = 18, scale = 6)
    private BigDecimal price;

    @Column(nullable = false)
    private Long qty;

    private OffsetDateTime createdAt;
    private OffsetDateTime triggeredAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.OPEN;

    private OffsetDateTime expiresAt;

    public GttOrder() { this.id = UUID.randomUUID(); this.createdAt = OffsetDateTime.now(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }

    public OrderSide getSide() { return side; }
    public void setSide(OrderSide side) { this.side = side; }

    public OrderType getType() { return type; }
    public void setType(OrderType type) { this.type = type; }

    public GttTriggerType getTriggerType() { return triggerType; }
    public void setTriggerType(GttTriggerType triggerType) { this.triggerType = triggerType; }

    public BigDecimal getTriggerPrice() { return triggerPrice; }
    public void setTriggerPrice(BigDecimal triggerPrice) { this.triggerPrice = triggerPrice; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Long getQty() { return qty; }
    public void setQty(Long qty) { this.qty = qty; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(OffsetDateTime triggeredAt) { this.triggeredAt = triggeredAt; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public OffsetDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; }
}
