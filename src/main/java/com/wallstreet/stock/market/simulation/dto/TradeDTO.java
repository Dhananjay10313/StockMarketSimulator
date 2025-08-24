package com.wallstreet.stock.market.simulation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object representing an executed trade.
 * This object carries all the necessary information about a trade that has occurred.
 */
@Getter
@Builder
@ToString
public class TradeDTO {

    /**
     * A unique identifier for the trade.
     */
    private final UUID tradeId;

    /**
     * The stock symbol that was traded (e.g., "AAPL").
     */
    private final String symbol;

    /**
     * The ID of the buy order that was part of the trade.
     */
    private final UUID buyOrderId;

    /**
     * The ID of the sell order that was part of the trade.
     */
    private final UUID sellOrderId;
    
    /**
     * The user ID of the buyer.
     */
    private final UUID buyerUserId;

    /**
     * The user ID of the seller.
     */
    private final UUID sellerUserId;

    /**
     * The price at which the trade was executed.
     */
    private final double price;

    /**
     * The number of shares that were traded.
     */
    private final long quantity;

    /**
     * The exact timestamp when the trade was executed.
     */
    private final Instant timestamp;
}
