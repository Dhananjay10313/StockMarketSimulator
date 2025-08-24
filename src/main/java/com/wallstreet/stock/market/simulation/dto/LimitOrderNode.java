package com.wallstreet.stock.market.simulation.dto;

import com.wallstreet.stock.market.simulation.dto.OrderDTO;

/**
 * A node in the doubly linked list, holding a LIMIT order.
 * This is structurally identical to GttOrderNode but is a distinct type
 * for clarity and future independent modification.
 */
public class LimitOrderNode {
    public final OrderDTO order;
    public LimitOrderNode prev;
    public LimitOrderNode next;

    public LimitOrderNode(OrderDTO order) {
        this.order = order;
    }
}
