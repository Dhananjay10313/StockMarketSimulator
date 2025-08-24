package com.wallstreet.stock.market.simulation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "stock_ticks")
public class StockTick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Assuming bigint NOT NULL DEFAULT nextval in DB
    private Long id;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "ts", nullable = false)
    private OffsetDateTime ts; // Changed to ts for consistency with your schema

    @Column(name = "price", nullable = false, precision = 18, scale = 6)
    private BigDecimal price;

    @Column(name = "volume") // Can be null, or set to 0 if no volume is known for synthetic ticks
    private Long volume;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    // Constructors (optional, but good practice)
    public StockTick() {}

    public StockTick(String symbol, OffsetDateTime ts, BigDecimal price, Long volume) {
        this.symbol = symbol;
        this.ts = ts;
        this.price = price;
        this.volume = volume;
        this.createdAt = OffsetDateTime.now(); // Set creation time when new tick is made
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public OffsetDateTime getTs() { return ts; }
    public void setTs(OffsetDateTime ts) { this.ts = ts; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
