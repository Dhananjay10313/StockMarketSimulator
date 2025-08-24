package com.wallstreet.stock.market.simulation.orderbook;

import com.wallstreet.stock.market.simulation.dto.OrderDTO;
import com.wallstreet.stock.market.simulation.dto.LimitOrderNode;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages LIMIT orders for a single stock using a doubly linked list.
 * This class provides the underlying storage. The actual matching logic
 * that requires price sorting will operate on this data.
 */
public class LimitOrderBook {

    private LimitOrderNode head;
    private LimitOrderNode tail;
    private final Map<UUID, LimitOrderNode> nodeMap = new HashMap<>();

    /**
     * Adds a new LIMIT order to the end of the book.
     * Note: This adds by time priority. Price priority will be handled
     * by the matching engine that reads from this book.
     */
    public synchronized void addOrder(OrderDTO order) {
        if (order.getType() != OrderDTO.Type.LIMIT) {
            throw new IllegalArgumentException("Only LIMIT orders can be added to the LimitOrderBook.");
        }
        LimitOrderNode newNode = new LimitOrderNode(order);
        nodeMap.put(order.getId(), newNode);

        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    /**
     * Removes a LIMIT order by its unique ID in O(1) time.
     */
    public synchronized boolean removeOrder(UUID orderId) {
        LimitOrderNode nodeToRemove = nodeMap.get(orderId);
        if (nodeToRemove == null) {
            return false;
        }

        if (nodeToRemove.prev != null) nodeToRemove.prev.next = nodeToRemove.next;
        else head = nodeToRemove.next;

        if (nodeToRemove.next != null) nodeToRemove.next.prev = nodeToRemove.prev;
        else tail = nodeToRemove.prev;

        nodeMap.remove(orderId);
        return true;
    }

    // Accessors for the matching engine to traverse the book
    public LimitOrderNode getHead() {
        return head;
    }
}
