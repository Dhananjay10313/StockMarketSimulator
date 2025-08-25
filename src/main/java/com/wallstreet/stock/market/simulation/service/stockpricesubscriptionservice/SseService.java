package com.wallstreet.stock.market.simulation.service.stockpricesubscriptionservice;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {

    private final SseSubscriptionManager subscriptionManager;
    
    // Key: The same unique client ID used in the SubscriptionManager
    // Value: The actual SseEmitter object to send data to the client
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseService(SseSubscriptionManager subscriptionManager) {
        this.subscriptionManager = subscriptionManager;
    }

    /**
     * Creates a new SSE connection and returns the emitter.
     * The client ID is generated here and must be used for all subsequent operations.
     * @return A configured SseEmitter instance.
     */
    public SseEmitter createEmitter() {
        // Set a long timeout, but the connection will be kept alive by heartbeats.
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        String clientId = UUID.randomUUID().toString();

        // Register the emitter so we can send data to it
        emitters.put(clientId, emitter);

        // Handle what happens on connection completion or timeout
        emitter.onCompletion(() -> removeClient(clientId));
        emitter.onTimeout(() -> removeClient(clientId));
        emitter.onError(e -> removeClient(clientId));

        // It's good practice to send an initial "connected" event with the client ID
        try {
            emitter.send(SseEmitter.event().name("connected").data(clientId));
        } catch (IOException e) {
            // This can happen if the client disconnects immediately
            removeClient(clientId);
        }

        return emitter;
    }

    /**
     * Subscribes a client to a list of stocks. Called from a REST Controller.
     * @param clientId The ID obtained from the "connected" event.
     * @param symbols The list of stock symbols.
     */
    public void subscribe(String clientId, Set<String> symbols) {
        subscriptionManager.subscribe(clientId, symbols);
    }

    /**
     * Unsubscribes a client. Called when the user navigates away or logs out.
     * @param clientId The ID of the client.
     */
    public void unsubscribe(String clientId) {
        subscriptionManager.unsubscribe(clientId);
    }
    
    /**
     * Pushes an LTP update to all relevant clients.
     * This method is the trigger, called from your LtpService or PostOrderProcessingService.
     * @param symbol The stock symbol that was updated.
     * @param price The new LTP.
     */
    public void pushLtpUpdate(String symbol, double price) {
        Set<String> clientIds = subscriptionManager.getSubscribersForSymbol(symbol);
        
        // Create the payload once
        Map<String, Double> payload = Map.of(symbol, price);

        for (String clientId : clientIds) {
            SseEmitter emitter = emitters.get(clientId);
            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event().name("ltp_update").data(payload));
                } catch (IOException e) {
                    // This error typically means the client has disconnected.
                    // The onCompletion/onError handler will clean up.
                    removeClient(clientId);
                }
            }
        }
    }

    /**
     * Centralized method to clean up both the emitter and the subscription.
     * @param clientId The client to remove.
     */
    private void removeClient(String clientId) {
        emitters.remove(clientId);
        subscriptionManager.unsubscribe(clientId);
    }
}
