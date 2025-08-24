package com.wallstreet.stock.market.simulation.service;

import com.wallstreet.stock.market.simulation.model.Order;
import com.wallstreet.stock.market.simulation.model.enums.OrderSide;
import com.wallstreet.stock.market.simulation.model.enums.OrderStatus;
import com.wallstreet.stock.market.simulation.model.Stock;
import com.wallstreet.stock.market.simulation.model.StockTick; // New import
import com.wallstreet.stock.market.simulation.repository.OrderRepository;
import com.wallstreet.stock.market.simulation.repository.StockRepository;
import com.wallstreet.stock.market.simulation.repository.StockTickRepository; // New import
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime; // New import
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class PriceCalculationService {

    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;
    private final StockTickRepository stockTickRepository; // Inject StockTickRepository
    private final Random random = new Random();

    @Value("${simulation.price.impact.factor:0.01}")
    private double priceImpactFactor;

    @Value("${simulation.price.volatility.base:0.005}")
    private double baseVolatility;

    public PriceCalculationService(OrderRepository orderRepository, StockRepository stockRepository, StockTickRepository stockTickRepository) {
        this.orderRepository = orderRepository;
        this.stockRepository = stockRepository;
        this.stockTickRepository = stockTickRepository; // Initialize
    }
    

    /**
     * Calculates and updates the synthetic current price for a given stock symbol.
     * This method incorporates base random walk, demand/supply imbalance, and other factors.
     * The method now fetches the stock and its current price internally.
     *
     * @param symbol The symbol of the stock for which to calculate and update the price.
     * @return The newly calculated price of the stock.
     */
    @Transactional // Ensure the stock update and tick insertion are atomic
    public BigDecimal calculateAndSetStockPrice(String symbol) {
        Optional<Stock> optionalStock = stockRepository.findById(symbol);
        if (optionalStock.isEmpty()) {
            throw new IllegalArgumentException("Stock with symbol " + symbol + " not found.");
        }
        Stock stock = optionalStock.get();

        // Get the current price from the Stock entity
        BigDecimal currentPrice = stock.getCurrentPrice();
        if (currentPrice == null) {
            // Handle case where currentPrice might be null initially (e.g., brand new stock)
            // You might want to set a default starting price or throw an error
            currentPrice = BigDecimal.valueOf(10.00); // Default starting price if null
            stock.setCurrentPrice(currentPrice); // Set it so next iteration has a base
            stockRepository.save(stock); // Persist this initial price
        }

        // 1. Calculate Demand and Supply
        List<OrderStatus> actionableStatuses = Arrays.asList(OrderStatus.OPEN, OrderStatus.PARTIAL);
        List<Order> actionableOrders = orderRepository.findBySymbolAndStatusIn(symbol, actionableStatuses);

        long totalDemand = 0;
        long totalSupply = 0;

        for (Order order : actionableOrders) {
            if (order.getSide() == OrderSide.BUY) {
                totalDemand += order.getRemainingQty();
            } else if (order.getSide() == OrderSide.SELL) {
                totalSupply += order.getRemainingQty();
            }
        }

        // 2. Calculate Order Imbalance
        double imbalance = 0.0;
        long totalLiquidity = totalDemand + totalSupply;
        if (totalLiquidity > 0) {
            imbalance = (double) (totalDemand - totalSupply) / totalLiquidity;
        }

        // 3. Apply Base Random Walk (Geometric Brownian Motion component)
        double drift = 0.0001; // Small positive drift for general market growth (configurable)
        double priceChangeFromRandomWalk = currentPrice.doubleValue() *
                                           (drift + baseVolatility * random.nextGaussian());

        // 4. Apply Imbalance-Based Price Adjustment
        double priceAdjustmentFromImbalance = imbalance * priceImpactFactor * currentPrice.doubleValue();

        // 5. Combine and Calculate New Price
        double newPriceDouble = currentPrice.doubleValue() + priceChangeFromRandomWalk + priceAdjustmentFromImbalance;

        // Ensure price doesn't go negative or too low (e.g., minimum price of 0.01)
        newPriceDouble = Math.max(0.01, newPriceDouble);

        BigDecimal newPrice = BigDecimal.valueOf(newPriceDouble).setScale(2, RoundingMode.HALF_UP);

        // 6. Update the stock's current_price in the 'stocks' table
        stock.setCurrentPrice(newPrice);
        stock.setUpdatedAt(OffsetDateTime.now()); // Update timestamp
        stockRepository.save(stock); // Persist the updated stock price

        // 7. Add a new entry to the 'stock_ticks' table
        StockTick newTick = new StockTick(symbol, OffsetDateTime.now(), newPrice, totalLiquidity); // Using totalLiquidity as synthetic volume for now
        stockTickRepository.save(newTick); // Save the new tick

        return newPrice;
    }

    /**
     * Calculates and updates prices for ALL active stocks.
     * This is typically called by a scheduled task.
     */
    @Transactional
    public Map<String, BigDecimal> calculateAndSetAllStockPrices() {
        List<Stock> allStocks = stockRepository.findAll();
        return allStocks.stream()
                .collect(Collectors.toMap(
                    Stock::getSymbol,
                    stock -> calculateAndSetStockPrice(stock.getSymbol()) // Calls the single-stock method
                ));
    }
}
