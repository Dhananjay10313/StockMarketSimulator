package com.wallstreet.stock.market.simulation.dto;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.wallstreet.stock.market.simulation.model.enums.OrderStatus;
import com.wallstreet.stock.market.simulation.model.enums.OrderType;
import com.wallstreet.stock.market.simulation.model.enums.OrderSide;

/**
 * A universal Data Transfer Object for all order types.
 */
public class OrderDTO {
    /**
     * Defines the condition for triggering an order.
     */
    public enum TriggerDirection {
        /**
         * Execute when the market price rises to or goes ABOVE the trigger price.
         * (Typically for Sell GTTs or Stop-Loss Buy orders).
         */
        ABOVE,
        /**
         * Execute when the market price falls to or goes BELOW the trigger price.
         * (Typically for Buy GTTs or Stop-Loss Sell orders).
         */
        BELOW
    }

    private UUID id;
    private  UUID userId;
    private  String symbol;
    private  OrderSide side;
    private  OrderType type;
    private long quantity;
    private OrderStatus status = OrderStatus.OPEN;


    // Fields that may not apply to all order types
    private Double price; // Null for MARKET orders
    private Double triggerPrice; // Null for non-GTT orders
    private TriggerDirection triggerDirection; // Null for non-GTT orders
    private Instant createdAt;

    // Constructor, Getters, and standard methods below...

    public OrderDTO(UUID id, UUID userId, String symbol, OrderSide side, OrderType type, long quantity, Double price, Double triggerPrice,
            TriggerDirection triggerDirection, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.symbol = symbol;
        this.side = side;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.triggerPrice = triggerPrice;
        this.triggerDirection = triggerDirection;
        this.createdAt = createdAt;
    }

    // Setter
    public void setId(UUID id) {
        this.id=id;
    }

    public void setCreatedAt(Instant createdAt){
        this.createdAt=createdAt;
    }

    // Getters

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderSide getSide() {
        return side;
    }

    public OrderType getType() {
        return type;
    }

    public long getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }

    public Double getTriggerPrice() {
        return triggerPrice;
    }

    public TriggerDirection getTriggerDirection() {
        return triggerDirection;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public OrderStatus getStatus() {
        return this.status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setQuantity(long quantity){
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OrderDTO orderDTO = (OrderDTO) o;
        return id.equals(orderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
