package com.wallstreet.stock.market.simulation.service;

import com.wallstreet.stock.market.simulation.dto.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * The main orchestrator for all order book operations.
 * It acts as a single entry point and delegates tasks to specialized
 * manager services based on the order type.
 */
@Service
public class OrderBookManager {

    private final GttOrderBookManagerService gttOrderBookManagerService;
    private final LimitOrderBookManagerService limitOrderBookManagerService;
    private final MarketOrderBookManagerService marketOrderBookManagerService;

    @Autowired
    public OrderBookManager(GttOrderBookManagerService gttOrderBookManagerService,
                            LimitOrderBookManagerService limitOrderBookManagerService,
                            MarketOrderBookManagerService marketOrderBookManagerService) {
        this.gttOrderBookManagerService = gttOrderBookManagerService;
        this.limitOrderBookManagerService = limitOrderBookManagerService;
        this.marketOrderBookManagerService = marketOrderBookManagerService;
    }

    /**
     * Receives any order and routes it to the correct service for processing.
     * @param order The order to be added to an order book.
     */
    public void addOrder(OrderDTO order) {
        switch (order.getType()) {
            case GTT:
                gttOrderBookManagerService.addGttOrder(order);
                break;
            case LIMIT:
                limitOrderBookManagerService.addLimitOrder(order);
                break;
            case MARKET:
                marketOrderBookManagerService.addMarketOrder(order);
                break;
            case IOC:
                System.out.println("IOC order handling is not yet implemented.");
                break;
            default:
                throw new IllegalArgumentException("Unsupported order type: " + order.getType());
        }
    }

    /**
     * Receives any order cancellation request and routes it to the correct service.
     * @param order The order details required for cancellation.
     */
    public boolean removeOrder(OrderDTO order) {
        switch (order.getType()) {
            case GTT:
                return gttOrderBookManagerService.removeGttOrder(order.getSymbol(), order.getSide(), order.getId());
            case LIMIT:
                return limitOrderBookManagerService.removeLimitOrder(order.getSymbol(), order.getSide(), order.getId());
            case MARKET:
                return marketOrderBookManagerService.removeMarketOrder(order.getSymbol(), order.getSide(), order.getId());
            default:
                return false;
        }
    }

    // --- NEWLY ADDED GETTER METHOD ---
    /**
     * Provides access to the MarketOrderBookManagerService.
     * @return The singleton instance of the MarketOrderBookManagerService.
     */
    public MarketOrderBookManagerService getMarketOrderBookManagerService() {
        return this.marketOrderBookManagerService;
    }
    
    // You can add getters for other managers here as they are needed.
}
