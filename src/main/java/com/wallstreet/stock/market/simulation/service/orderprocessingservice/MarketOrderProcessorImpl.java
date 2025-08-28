package com.wallstreet.stock.market.simulation.service.orderprocessingservice;

import com.wallstreet.stock.market.simulation.dto.OrderDTO;
import com.wallstreet.stock.market.simulation.model.enums.OrderStatus;
import com.wallstreet.stock.market.simulation.model.enums.OrderSide;
import com.wallstreet.stock.market.simulation.model.enums.OrderSide;
import com.wallstreet.stock.market.simulation.dto.TradeDTO;
import com.wallstreet.stock.market.simulation.orderbook.MarketOrderBook;
import com.wallstreet.stock.market.simulation.service.MarketOrderBookManagerService;
import com.wallstreet.stock.market.simulation.service.influxservice.InfluxService;
import com.wallstreet.stock.market.simulation.service.ltp.LtpService;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Component
public class MarketOrderProcessorImpl implements OrderProcessor {

    private static final Duration MARKET_ORDER_EXPIRY = Duration.ofMinutes(5);


    @Override
    public ProcessingResult process(ProcessingContext context) {
        ProcessingResult result = new ProcessingResult();
        MarketOrderBookManagerService marketManager = context.getOrderBookManager().getMarketOrderBookManagerService();
        LtpService ltpService = context.getLtpService();
        InfluxService influxService = context.getInfluxService();

        Set<String> activeSymbols = marketManager.getAllBuyBooks().keySet();

        for (String symbol : activeSymbols) {
            processOrdersForSymbol(symbol, context, result, marketManager, ltpService, influxService);
        }
        return result;
    }

    private void processOrdersForSymbol(String symbol, ProcessingContext context, ProcessingResult result, MarketOrderBookManagerService marketManager, LtpService ltpService, InfluxService influxService) {
        MarketOrderBook buyBook = marketManager.getBuyBook(symbol);
        MarketOrderBook sellBook = marketManager.getSellBook(symbol);

        while (buyBook != null && !buyBook.isEmpty() && sellBook != null && !sellBook.isEmpty()) {
            OrderDTO marketBuyOrder = marketManager.peekOldestBuyOrder(symbol);

            if (isOrderExpired(marketBuyOrder, context.getCycleTimestamp())) {
                marketBuyOrder.setStatus(OrderStatus.EXPIRED);
                marketManager.removeMarketOrder(symbol, OrderSide.BUY, marketBuyOrder.getId());
                result.addProcessedOrder(marketBuyOrder);
                continue;
            }

            // Point 1: Fetching the lowest price sell order as requested.
            OrderDTO lowestPriceSellOrder = marketManager.getLowestPriceSellOrder(symbol);
            if (lowestPriceSellOrder == null) {
                break; // No opposing orders left.
            }

            if (isOrderExpired(lowestPriceSellOrder, context.getCycleTimestamp())) {
                lowestPriceSellOrder.setStatus(OrderStatus.EXPIRED);
                marketManager.removeMarketOrder(symbol, OrderSide.SELL, lowestPriceSellOrder.getId());
                result.addProcessedOrder(lowestPriceSellOrder);
                continue; // Find the next lowest priced sell order.
            }

            executeMatch(marketBuyOrder, lowestPriceSellOrder, result, marketManager, ltpService, influxService);
        }
    }

    private void executeMatch(OrderDTO buyOrder, OrderDTO sellOrder, ProcessingResult result, MarketOrderBookManagerService marketManager, LtpService ltpService, InfluxService influxService) {
        // Point 2: Corrected execution price logic.
        double executionPrice;
        if (buyOrder.getCreatedAt().isBefore(sellOrder.getCreatedAt())) {
            // Buy order is older (resting), so execution price is from the newer sell order.
            executionPrice = sellOrder.getPrice();
        } else {
            // Sell order is older (resting), so execution price is from the newer buy order.
            executionPrice = buyOrder.getPrice();
        }

        long executionQty = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());

        
        //Updating LTP of stock
        ltpService.updateLtp(buyOrder.getSymbol(), executionPrice);

        // Inserting value to influxdb new stock price record 
        influxService.insertStockPriceRecord(buyOrder.getSymbol(), executionPrice, (int)executionQty, Instant.now());

        // Point 3: Assumes TradeDTO exists elsewhere. The builder is called here.
        TradeDTO trade = TradeDTO.builder()
            .tradeId(UUID.randomUUID())
            .symbol(buyOrder.getSymbol())
            .buyOrderId(buyOrder.getId())
            .sellOrderId(sellOrder.getId())
            .price(executionPrice)
            .quantity(executionQty)
            .timestamp(Instant.now())
            .build();
            
        result.addTrade(trade);

        buyOrder.setQuantity(buyOrder.getQuantity() - executionQty);
        sellOrder.setQuantity(sellOrder.getQuantity() - executionQty);

        updateAndRecordOrderStatus(buyOrder, result, marketManager);
        updateAndRecordOrderStatus(sellOrder, result, marketManager);
    }

    private void updateAndRecordOrderStatus(OrderDTO order, ProcessingResult result, MarketOrderBookManagerService marketManager) {
        if (order.getQuantity() == 0) {
            order.setStatus(OrderStatus.FILLED);
            marketManager.removeMarketOrder(order.getSymbol(), order.getSide(), order.getId());
        } else {
            order.setStatus(OrderStatus.PARTIAL);
        }
        result.addProcessedOrder(order);
    }

    private boolean isOrderExpired(OrderDTO order, Instant currentTime) {
        return order.getCreatedAt().plus(MARKET_ORDER_EXPIRY).isBefore(currentTime);
    }
}
