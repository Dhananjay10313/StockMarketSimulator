package com.wallstreet.stock.market.simulation.service.ltp;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory, thread-safe implementation of the LtpService.
 * Uses a ConcurrentHashMap to store and manage stock LTPs.
 */
@Service
public class LtpServiceImpl implements LtpService {

    /**
     * The core data structure for storing LTPs.
     * Key: Stock Symbol (String)
     * Value: Last Traded Price (Double)
     * ConcurrentHashMap is used for high-performance, thread-safe access.
     */
    private final ConcurrentHashMap<String, Double> ltpStore = new ConcurrentHashMap<>();

    @Override
    public void updateLtp(String symbol, double price) {
        if (symbol == null || symbol.trim().isEmpty()) {
            // In a real application, you might throw an IllegalArgumentException
            // or log a warning. For now, we'll simply ignore invalid input.
            return;
        }
        ltpStore.put(symbol, price);
    }

    @Override
    public Optional<Double> getLtp(String symbol) {
        if (symbol == null) {
            return Optional.empty();
        }
        // .ofNullable handles cases where the symbol might not be in the map
        return Optional.ofNullable(ltpStore.get(symbol));
    }

    @Override
    public Map<String, Double> getAllLtps() {
        // Return an unmodifiable view of the map to prevent external modification.
        // This is a defensive copy, ensuring the caller cannot alter the internal state.
        return Collections.unmodifiableMap(ltpStore);
    }
}
