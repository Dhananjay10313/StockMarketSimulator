package com.wallstreet.stock.market.simulation.dto;

public class GttOrderNode {
    public final OrderDTO order;
    public GttOrderNode prev;
    public GttOrderNode next;

    public GttOrderNode(OrderDTO order) {
        this.order = order;
    }
}