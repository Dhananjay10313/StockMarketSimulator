package com.wallstreet.stock.market.simulation.dto;

import com.wallstreet.stock.market.simulation.dto.OrderDTO;

/**
 * A node in the doubly linked list, holding a MARKET order.
 * This creates a distinct type for market orders in the system.
 */
public class MarketOrderNode {
    public final OrderDTO order;
    public MarketOrderNode prev;
    public MarketOrderNode next;

    public MarketOrderNode(OrderDTO order) {
        this.order = order;
    }
}
