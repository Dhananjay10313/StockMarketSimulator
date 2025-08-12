package com.wallstreet.stock.market.simulation.model;
import com.wallstreet.stock.market.simulation.model.enums.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "trades", indexes = {
        @Index(name = "idx_trades_symbol_ts", columnList = "symbol, timestamp DESC")
})
public class Trade {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "buy_order_id")
    private Order buyOrder;

    @ManyToOne
    @JoinColumn(name = "sell_order_id")
    private Order sellOrder;

    @ManyToOne(optional = false)
    @JoinColumn(name = "symbol")
    private Stock stock;

    @Column(precision = 18, scale = 6, nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Long qty;

    private OffsetDateTime timestamp;

    public Trade() { this.id = UUID.randomUUID(); this.timestamp = OffsetDateTime.now(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Order getBuyOrder() { return buyOrder; }
    public void setBuyOrder(Order buyOrder) { this.buyOrder = buyOrder; }

    public Order getSellOrder() { return sellOrder; }
    public void setSellOrder(Order sellOrder) { this.sellOrder = sellOrder; }

    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Long getQty() { return qty; }
    public void setQty(Long qty) { this.qty = qty; }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
}
