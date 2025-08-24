package com.wallstreet.stock.market.simulation.service.ltp;

import java.util.Map;
import java.util.Optional;

/**
 * Service interface for managing the Last Traded Price (LTP) of stocks.
 * Provides a thread-safe mechanism to update and retrieve stock prices.
 */
public interface LtpService {

    /**
     * Updates the Last Traded Price (LTP) for a specific stock symbol.
     *
     * @param symbol The stock symbol (e.g., "AAPL"). Must not be null.
     * @param price  The new LTP of the stock.
     */
    void updateLtp(String symbol, double price);

    /**
     * Retrieves the Last Traded Price (LTP) for a specific stock symbol.
     *
     * @param symbol The stock symbol to query. Must not be null.
     * @return An Optional containing the LTP if it exists, otherwise an empty Optional.
     */
    Optional<Double> getLtp(String symbol);

    /**
     * Retrieves a snapshot of the Last Traded Prices for all tracked stocks.
     * The returned map is a copy and immutable to ensure thread safety.
     *
     * @return An unmodifiable Map containing all current stock symbols and their LTPs.
     */
    Map<String, Double> getAllLtps();
}
