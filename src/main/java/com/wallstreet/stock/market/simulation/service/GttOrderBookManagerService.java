package com.wallstreet.stock.market.simulation.service;

import com.wallstreet.stock.market.simulation.dto.OrderDTO;
import com.wallstreet.stock.market.simulation.model.enums.OrderSide;
import com.wallstreet.stock.market.simulation.model.enums.OrderType;
import com.wallstreet.stock.market.simulation.orderbook.GttOrderBook;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A specialized service responsible ONLY for managing GTT (Good-Till-Triggered) order books.
 * It maintains separate books for buy and sell orders for each stock symbol.
 */
@Service
public class GttOrderBookManagerService {

    private final Map<String, GttOrderBook> buyGttOrderBooks = new ConcurrentHashMap<>();
    private final Map<String, GttOrderBook> sellGttOrderBooks = new ConcurrentHashMap<>();

    /**
     * Adds a GTT order to the appropriate buy or sell book.
     * Throws an exception if the order is not a GTT type.
     *
     * @param order The GTT order to add.
     */
    public void addGttOrder(OrderDTO order) {
        if (order.getType() != OrderType.GTT) {
            throw new IllegalArgumentException("This service only accepts GTT orders. Received: " + order.getType());
        }

        Map<String, GttOrderBook> targetBookMap = (order.getSide() == OrderSide.BUY) ? buyGttOrderBooks : sellGttOrderBooks;

        GttOrderBook orderBook = targetBookMap.computeIfAbsent(order.getSymbol(), k -> new GttOrderBook());
        orderBook.addOrder(order);
    }

    /**
     * Removes a GTT order from the books.
     *
     * @param symbol The stock symbol.
     * @param side   The side of the order (BUY/SELL) to locate the correct book.
     * @param orderId The UUID of the order to remove.
     * @return true if the order was found and removed, false otherwise.
     */
    public boolean removeGttOrder(String symbol, OrderSide side, UUID orderId) {
        Map<String, GttOrderBook> targetBookMap = (side == OrderSide.BUY) ? buyGttOrderBooks : sellGttOrderBooks;
        GttOrderBook orderBook = targetBookMap.get(symbol);

        if (orderBook != null) {
            return orderBook.removeOrder(orderId);
        }
        return false;
    }

    // --- Accessor methods for other services (e.g., GTT Monitoring Service) ---

    public GttOrderBook getBuyBook(String symbol) {
        return buyGttOrderBooks.get(symbol);
    }

    public GttOrderBook getSellBook(String symbol) {
        return sellGttOrderBooks.get(symbol);
    }
}
