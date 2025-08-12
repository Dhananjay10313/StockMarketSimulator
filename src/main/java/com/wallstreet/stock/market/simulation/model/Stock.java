package com.wallstreet.stock.market.simulation.model;
import com.wallstreet.stock.market.simulation.model.enums.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "stocks")
public class Stock {
    @Id
    private String symbol; // PK

    @Column(nullable = false)
    private String name;

    private String exchange;

    private Integer lotSize = 1;

    private Boolean isActive = true;

    @Column(columnDefinition = "jsonb")
    private String metadata; 

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Stock() { this.createdAt = OffsetDateTime.now(); this.updatedAt = OffsetDateTime.now(); }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public Integer getLotSize() { return lotSize; }
    public void setLotSize(Integer lotSize) { this.lotSize = lotSize; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
