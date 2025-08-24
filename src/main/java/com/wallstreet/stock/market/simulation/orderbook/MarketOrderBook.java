package com.wallstreet.stock.market.simulation.orderbook;

import com.wallstreet.stock.market.simulation.dto.OrderDTO;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Manages MARKET orders for a single stock symbol.
 * This class uses a dual data structure approach:
 * 1. A TreeMap (`ordersByPrice`) for fast, price-sorted lookups (O(log n)).
 * 2. A Queue (`fifoQueue`) to maintain strict arrival order (FIFO) for processing.
 */

public class MarketOrderBook {

    // Structure 1: For price-based lookups (lowest/highest price).
    private final TreeMap<Double, Queue<OrderDTO>> ordersByPrice = new TreeMap<>();
    
    // Structure 2: For FIFO (First-In, First-Out) processing.
    private final Queue<OrderDTO> fifoQueue = new LinkedList<>();

    // Helper map for O(1) direct access for fast removal by ID.
    private final Map<UUID, OrderDTO> ordersById = new HashMap<>();


    public synchronized boolean isEmpty() {
        return ordersById.isEmpty();
    }

    /**
     * Adds a new MARKET order to all internal data structures.
     * @param order The market order to add.
     */
    public synchronized void addOrder(OrderDTO order) {
        if (order.getType() != OrderDTO.Type.MARKET) {
            throw new IllegalArgumentException("Only MARKET orders can be added to this book.");
        }
        if (order.getPrice() == null) {
            throw new IllegalArgumentException("Market orders must have a price to be stored in this book.");
        }

        // Add to all three structures
        ordersById.put(order.getId(), order);
        ordersByPrice.computeIfAbsent(order.getPrice(), k -> new LinkedList<>()).add(order);
        fifoQueue.add(order);
    }

    /**
     * Removes a MARKET order from all internal data structures by its unique ID.
     * @param orderId The UUID of the order to remove.
     * @return true if the order was found and removed, false otherwise.
     */
    public synchronized boolean removeOrder(UUID orderId) {
        OrderDTO orderToRemove = ordersById.remove(orderId);
        if (orderToRemove == null) {
            return false;
        }

        // Remove from price-sorted map
        Queue<OrderDTO> priceQueue = ordersByPrice.get(orderToRemove.getPrice());
        if (priceQueue != null) {
            priceQueue.remove(orderToRemove);
            if (priceQueue.isEmpty()) {
                ordersByPrice.remove(orderToRemove.getPrice());
            }
        }
        
        // Remove from FIFO queue. Note: This is an O(n) operation.
        // It's acceptable for cancellations, but processing should use pollOldestOrder().
        fifoQueue.remove(orderToRemove);

        return true;
    }

    // --- FIFO-based Methods (Primary for processing) ---

    /**
     * Retrieves the oldest order (head of the FIFO queue) without removing it.
     * @return The oldest OrderDTO, or null if the book is empty.
     */
    public synchronized OrderDTO peekOldestOrder() {
        return fifoQueue.peek();
    }

    /**
     * Removes and returns the oldest order from the book (from all structures).
     * This is the primary method the processing service should use.
     * @return The oldest OrderDTO, or null if the book is empty.
     */
    public synchronized OrderDTO pollOldestOrder() {
        // Get the oldest order from the FIFO queue
        OrderDTO oldestOrder = fifoQueue.poll();
        if (oldestOrder == null) {
            return null;
        }

        // Clean it up from the other maps
        ordersById.remove(oldestOrder.getId());
        Queue<OrderDTO> priceQueue = ordersByPrice.get(oldestOrder.getPrice());
        if (priceQueue != null) {
            priceQueue.remove(oldestOrder);
            if (priceQueue.isEmpty()) {
                ordersByPrice.remove(oldestOrder.getPrice());
            }
        }
        
        return oldestOrder;
    }

    // --- Price-based Methods (For analytics or specific strategies) ---

    public synchronized OrderDTO getLowestPriceOrder() {
        if (ordersByPrice.isEmpty()) return null;
        return ordersByPrice.firstEntry().getValue().peek();
    }

    public synchronized OrderDTO getHighestPriceOrder() {
        if (ordersByPrice.isEmpty()) return null;
        return ordersByPrice.lastEntry().getValue().peek();
    }
    
    /**
     * Removes and returns the order with the lowest price.
     *
     * @return The lowest priced order, or null if the book is empty.
     */
    public synchronized OrderDTO pollLowestPriceOrder() {
        if (ordersByPrice.isEmpty()) {
            return null;
        }
        Map.Entry<Double, Queue<OrderDTO>> firstEntry = ordersByPrice.firstEntry();
        Queue<OrderDTO> lowestPriceQueue = firstEntry.getValue();
        OrderDTO lowestPriceOrder = lowestPriceQueue.poll();

        if (lowestPriceOrder != null) {
            ordersById.remove(lowestPriceOrder.getId());
            if (lowestPriceQueue.isEmpty()) {
                ordersByPrice.remove(firstEntry.getKey());
            }
        }
        return lowestPriceOrder;
    }
}
