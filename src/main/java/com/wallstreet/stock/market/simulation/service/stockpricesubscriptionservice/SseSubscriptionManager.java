package com.wallstreet.stock.market.simulation.service.stockpricesubscriptionservice;

import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseSubscriptionManager {

    // Key: A unique identifier for the SSE connection (e.g., UUID)
    // Value: A thread-safe Set of stock symbols the client is subscribed to.
    private final ConcurrentHashMap<String, Set<String>> subscriptions = new ConcurrentHashMap<>();

    /**
     * Subscribes a client to a set of stock symbols.
     * @param clientId A unique ID for the client connection.
     * @param symbols The set of symbols to subscribe to.
     */
    public void subscribe(String clientId, Set<String> symbols) {
        // Use a thread-safe set for the symbols
        Set<String> subscribedSymbols = ConcurrentHashMap.newKeySet();
        subscribedSymbols.addAll(symbols);
        subscriptions.put(clientId, subscribedSymbols);
    }

    /**
     * Unsubscribes a client from all symbols, effectively ending their session.
     * @param clientId The unique ID of the client to remove.
     */
    public void unsubscribe(String clientId) {
        subscriptions.remove(clientId);
    }

    /**
     * Finds all client IDs that are subscribed to a specific stock symbol.
     * @param symbol The stock symbol to check.
     * @return A Set of client IDs subscribed to the symbol.
     */
    public Set<String> getSubscribersForSymbol(String symbol) {
        Set<String> subscribers = ConcurrentHashMap.newKeySet();
        subscriptions.forEach((clientId, symbols) -> {
            if (symbols.contains(symbol)) {
                subscribers.add(clientId);
            }
        });
        return subscribers;
    }
}
