package com.wallstreet.stock.market.simulation.orderbook;

import com.wallstreet.stock.market.simulation.dto.OrderDTO;
import com.wallstreet.stock.market.simulation.model.enums.OrderType;
import com.wallstreet.stock.market.simulation.dto.GttOrderNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * A node in the doubly linked list, holding the GTT order.
 */


/**
 * Manages GTT orders for a single stock using a doubly linked list.
 * This structure is optimized for fast removals and easy traversal.
 */
public class GttOrderBook {
    private GttOrderNode head;
    private GttOrderNode tail;

    // A map for O(1) lookup of any node by its order ID, crucial for fast removals.
    private final Map<UUID, GttOrderNode> nodeMap = new HashMap<>();

    /**
     * Adds a new GTT order to the end of the book.
     * This is a thread-safe O(1) operation.
     * @param order The GTT order to add.
     */
    public synchronized void addOrder(OrderDTO order) {
        if (order.getType() != OrderType.GTT) {
            throw new IllegalArgumentException("Only GTT orders can be added to the GttOrderBook.");
        }
        GttOrderNode newNode = new GttOrderNode(order);
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
     * Removes a GTT order from the book by its unique ID.
     * This is a thread-safe O(1) operation thanks to the internal map.
     * @param orderId The UUID of the order to remove.
     * @return true if the order was found and removed, false otherwise.
     */
    public synchronized boolean removeOrder(UUID orderId) {
        GttOrderNode nodeToRemove = nodeMap.get(orderId);
        if (nodeToRemove == null) {
            return false;
        }

        // Unlink the node
        if (nodeToRemove.prev != null) nodeToRemove.prev.next = nodeToRemove.next;
        else head = nodeToRemove.next; // It was the head

        if (nodeToRemove.next != null) nodeToRemove.next.prev = nodeToRemove.prev;
        else tail = nodeToRemove.prev; // It was the tail

        nodeMap.remove(orderId);
        return true;
    }

    /**
     * Provides an iterator to easily traverse all GTT orders in the book.
     * This is the primary method for the GTT monitoring service to check for triggers.
     * @return An iterator over the GTT orders.
     */
    public synchronized Iterator<OrderDTO> getOrderIterator() {
        return new Iterator<>() {
            private GttOrderNode current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public OrderDTO next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                OrderDTO order = current.order;
                current = current.next;
                return order;
            }
        };
    }

    /**
     * Returns the number of pending GTT orders in the book.
     * @return The size of the order book.
     */
    public synchronized int size() {
        return nodeMap.size();
    }
}
