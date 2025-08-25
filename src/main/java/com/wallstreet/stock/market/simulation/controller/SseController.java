package com.wallstreet.stock.market.simulation.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Set;

import com.wallstreet.stock.market.simulation.service.stockpricesubscriptionservice.SseService;

@RestController
@RequestMapping("/api/sse")
public class SseController {

    private final SseService sseService;

    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

    /**
     * The endpoint the frontend connects to for receiving real-time updates.
     */
    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        return sseService.createEmitter();
    }

    /**
     * The endpoint for the frontend to subscribe to a list of stock symbols.
     */
    @PostMapping("/subscribe")
    public ResponseEntity<Void> subscribe(@RequestBody SubscriptionRequest request) {
        sseService.subscribe(request.getClientId(), request.getSymbols());
        return ResponseEntity.ok().build();
    }
    
    /**
     * The endpoint for the frontend to unsubscribe.
     */
    @PostMapping("/unsubscribe")
    public ResponseEntity<Void> unsubscribe(@RequestBody SubscriptionRequest request) {
        sseService.unsubscribe(request.getClientId());
        return ResponseEntity.ok().build();
    }

    // A simple DTO for the request body
    static class SubscriptionRequest {
        private String clientId;
        private Set<String> symbols;

        // Getters and Setters
        public String getClientId() { return clientId; }
        public void setClientId(String clientId) { this.clientId = clientId; }
        public Set<String> getSymbols() { return symbols; }
        public void setSymbols(Set<String> symbols) { this.symbols = symbols; }
    }
}

