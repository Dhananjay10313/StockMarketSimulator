package com.wallstreet.stock.market.simulation.model;
import com.wallstreet.stock.market.simulation.model.enums.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "stock_candles", uniqueConstraints = {@UniqueConstraint(columnNames = {"symbol","interval","start_ts"})},
       indexes = { @Index(name = "idx_stock_candles_symbol_interval_ts", columnList = "symbol, interval, start_ts DESC") })
public class StockCandle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "symbol")
    private Stock stock;

    private String interval;
    private OffsetDateTime startTs;

    @Column(precision = 18, scale = 6, nullable = false)
    private BigDecimal open;
    @Column(precision = 18, scale = 6, nullable = false)
    private BigDecimal high;
    @Column(precision = 18, scale = 6, nullable = false)
    private BigDecimal low;
    @Column(precision = 18, scale = 6, nullable = false)
    private BigDecimal close;

    private Long volume = 0L;
    private OffsetDateTime createdAt;

    public StockCandle() { this.createdAt = OffsetDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }

    public String getInterval() { return interval; }
    public void setInterval(String interval) { this.interval = interval; }

    public OffsetDateTime getStartTs() { return startTs; }
    public void setStartTs(OffsetDateTime startTs) { this.startTs = startTs; }

    public BigDecimal getOpen() { return open; }
    public void setOpen(BigDecimal open) { this.open = open; }

    public BigDecimal getHigh() { return high; }
    public void setHigh(BigDecimal high) { this.high = high; }

    public BigDecimal getLow() { return low; }
    public void setLow(BigDecimal low) { this.low = low; }

    public BigDecimal getClose() { return close; }
    public void setClose(BigDecimal close) { this.close = close; }

    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
