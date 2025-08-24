package com.wallstreet.stock.market.simulation.service;

import com.wallstreet.stock.market.simulation.dto.OrderDTO;
import com.wallstreet.stock.market.simulation.orderbook.MarketOrderBook;
import com.wallstreet.stock.market.simulation.model.enums.OrderSide;
import com.wallstreet.stock.market.simulation.model.enums.OrderType;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MarketOrderBookManagerService {

    private final Map<String, MarketOrderBook> buyMarketOrderBooks = new ConcurrentHashMap<>();
    private final Map<String, MarketOrderBook> sellMarketOrderBooks = new ConcurrentHashMap<>();

    public void addMarketOrder(OrderDTO order) {
        if (order.getType() != OrderType.MARKET) {
            throw new IllegalArgumentException("This service only accepts MARKET orders.");
        }
        Map<String, MarketOrderBook> targetBookMap = (order.getSide() == OrderSide.BUY)
                ? buyMarketOrderBooks
                : sellMarketOrderBooks;
        MarketOrderBook orderBook = targetBookMap.computeIfAbsent(order.getSymbol(), k -> new MarketOrderBook());
        orderBook.addOrder(order);
    }

    public boolean removeMarketOrder(String symbol, OrderSide side, UUID orderId) {
        Map<String, MarketOrderBook> targetBookMap = (side == OrderSide.BUY)
                ? buyMarketOrderBooks
                : sellMarketOrderBooks;
        MarketOrderBook orderBook = targetBookMap.get(symbol);
        if (orderBook != null) {
            return orderBook.removeOrder(orderId);
        }
        return false;
    }

    // --- NEWLY ADDED GETTER METHODS ---
    /**
     * Retrieves the entire buy-side market order book for a given symbol.
     * @param symbol The stock symbol.
     * @return The MarketOrderBook instance, or null if no such book exists.
     */
    public MarketOrderBook getBuyBook(String symbol) {
        return buyMarketOrderBooks.get(symbol);
    }

    /**
     * Retrieves the entire sell-side market order book for a given symbol.
     * @param symbol The stock symbol.
     * @return The MarketOrderBook instance, or null if no such book exists.
     */
    public MarketOrderBook getSellBook(String symbol) {
        return sellMarketOrderBooks.get(symbol);
    }
    
    /**
     * Provides a set of all symbols that have active buy orders.
     * @return An unmodifiable map of the buy order books.
     */
    public Map<String, MarketOrderBook> getAllBuyBooks() {
        return Collections.unmodifiableMap(buyMarketOrderBooks);
    }

    // --- FIFO and Price-based methods from your file would follow here ---
    
    public OrderDTO peekOldestBuyOrder(String symbol) {
        MarketOrderBook book = buyMarketOrderBooks.get(symbol);
        return (book != null) ? book.peekOldestOrder() : null;
    }

    public OrderDTO peekOldestSellOrder(String symbol) {
        MarketOrderBook book = sellMarketOrderBooks.get(symbol);
        return (book != null) ? book.peekOldestOrder() : null;
    }
    
    public OrderDTO getLowestPriceSellOrder(String symbol) {
        MarketOrderBook book = sellMarketOrderBooks.get(symbol);
        return (book != null) ? book.getLowestPriceOrder() : null;
    }

    // ... other methods like pollOldestBuyOrder, getHighestPriceBuyOrder etc.
}
