package com.wallstreet.stock.market.simulation.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "trades", indexes = {
        @Index(name = "idx_trades_symbol_ts", columnList = "symbol, timestamp DESC"),
        @Index(name = "idx_trades_order", columnList = "buy_order_id, sell_order_id")
})
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "buy_order_id")
    private UUID buyOrderId;

    @Column(name = "sell_order_id")
    private UUID sellOrderId;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "price", precision = 18, scale = 6, nullable = false)
    private BigDecimal price;

    @Column(name = "qty", nullable = false)
    private Long qty;

    @CreationTimestamp
    @Column(name = "timestamp", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime timestamp;

    // JPA requires a no-arg constructor
    public Trade() {
    }

    // --- Getters and Setters ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBuyOrderId() {
        return buyOrderId;
    }

    public void setBuyOrderId(UUID buyOrderId) {
        this.buyOrderId = buyOrderId;
    }

    public UUID getSellOrderId() {
        return sellOrderId;
    }

    public void setSellOrderId(UUID sellOrderId) {
        this.sellOrderId = sellOrderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
