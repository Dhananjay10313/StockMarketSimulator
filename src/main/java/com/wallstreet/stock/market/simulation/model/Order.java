package com.wallstreet.stock.market.simulation.model;

import com.wallstreet.stock.market.simulation.model.enums.OrderSide;
import com.wallstreet.stock.market.simulation.model.enums.OrderType;
import com.wallstreet.stock.market.simulation.model.enums.OrderStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "orders") // Maps to the public.orders table
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Adjust if your UUID generation strategy differs (e.g., specific to DB)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @Column(name = "symbol", nullable = false)
    private String symbol; // This matches the 'symbol' property for JPA queries

    @Enumerated(EnumType.STRING) // Still include for general JPA compatibility
    @JdbcTypeCode(SqlTypes.NAMED_ENUM) // Important: tells Hibernate to map to a named SQL ENUM type
    @Column(name = "side", nullable = false)
    private OrderSide side;

    @Enumerated(EnumType.STRING) // Still include for general JPA compatibility
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "type", nullable = false)
    private OrderType type;

    @Enumerated(EnumType.STRING) // Still include for general JPA compatibility
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "price", precision = 18, scale = 6) // Matches numeric(18, 6)
    private BigDecimal price;

    @Column(name = "qty", nullable = false)
    private Long qty; // bigint in postgres maps to Long in Java

    @Column(name = "remaining_qty", nullable = false)
    private Long remainingQty;
    
    @Column(name = "created_at")
    private OffsetDateTime createdAt; // timestamp with time zone

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt; // timestamp with time zone

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt; // timestamp with time zone

    @Column(name = "source")
    private String source;

    @Column(name = "version", nullable = false)
    private Long version; // bigint

    // --- Constructors (Optional, but often useful) ---
    public Order() {
    }

    // You can add a constructor for creating new orders
    public Order(User userId, String symbol, OrderSide side, OrderType type, BigDecimal price, Long qty, Long remainingQty, OrderStatus status, String source) {
        this.userId = userId;
        this.symbol = symbol;
        this.side = side;
        this.type = type;
        this.price = price;
        this.qty = qty;
        this.remainingQty = remainingQty;
        this.status = status;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        this.version = 1L; // Initial version
        this.source = source;
    }


    // --- Getters and Setters ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUserId() {
        return userId;
    }

    public void setUser(User userId) {
        this.userId = userId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public OrderSide getSide() {
        return side;
    }

    public void setSide(OrderSide side) {
        this.side = side;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public Long getRemainingQty() {
        return remainingQty;
    }

    public void setRemainingQty(Long remainingQty) {
        this.remainingQty = remainingQty;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
