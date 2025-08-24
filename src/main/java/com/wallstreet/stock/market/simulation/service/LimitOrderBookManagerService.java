package com.wallstreet.stock.market.simulation.service;

import com.wallstreet.stock.market.simulation.dto.OrderDTO;
import com.wallstreet.stock.market.simulation.model.enums.OrderSide;
import com.wallstreet.stock.market.simulation.orderbook.LimitOrderBook;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A specialized service responsible ONLY for managing LIMIT order books.
 */
@Service
public class LimitOrderBookManagerService {

    private final Map<String, LimitOrderBook> buyLimitOrderBooks = new ConcurrentHashMap<>();
    private final Map<String, LimitOrderBook> sellLimitOrderBooks = new ConcurrentHashMap<>();

    /**
     * Adds a LIMIT order to the appropriate buy or sell book.
     */
    public void addLimitOrder(OrderDTO order) {
        if (order.getType() != OrderDTO.Type.LIMIT) {
            throw new IllegalArgumentException("This service only accepts LIMIT orders.");
        }

        Map<String, LimitOrderBook> targetBookMap = (order.getSide() == OrderSide.BUY) ? buyLimitOrderBooks : sellLimitOrderBooks;

        LimitOrderBook orderBook = targetBookMap.computeIfAbsent(order.getSymbol(), k -> new LimitOrderBook());
        orderBook.addOrder(order);
    }

    /**
     * Removes a LIMIT order from the books.
     */
    public boolean removeLimitOrder(String symbol, OrderSide side, UUID orderId) {
        Map<String, LimitOrderBook> targetBookMap = (side == OrderSide.BUY) ? buyLimitOrderBooks : sellLimitOrderBooks;
        LimitOrderBook orderBook = targetBookMap.get(symbol);

        if (orderBook != null) {
            return orderBook.removeOrder(orderId);
        }
        return false;
    }

    // --- Accessor methods for the Matching Engine ---
    public LimitOrderBook getBuyBook(String symbol) {
        return buyLimitOrderBooks.get(symbol);
    }

    public LimitOrderBook getSellBook(String symbol) {
        return sellLimitOrderBooks.get(symbol);
    }
}
