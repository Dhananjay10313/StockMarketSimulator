package com.wallstreet.stock.market.simulation.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.wallstreet.stock.market.simulation.service.influxservice.InfluxService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class StockPriceUpdateWorker {

    private static final Logger logger = LoggerFactory.getLogger(StockPriceUpdateWorker.class);
    private final PriceCalculationService priceCalculationService;

    public StockPriceUpdateWorker(PriceCalculationService priceCalculationService, InfluxService influxService) {
        this.priceCalculationService = priceCalculationService;
    }

    /**
     * Scheduled task to update all stock prices based on supply/demand and random walk.
     * This will run every 1 second (1000 milliseconds).
     */
    @Scheduled(fixedRate = 1000) // Runs every 1 second
    public void updateStockPricesEverySecond() {
        logger.info("Running stock price update worker...");
        try {
            // Call method to update all stock prices
            priceCalculationService.calculateAndSetAllStockPrices();
            logger.info("Successfully updated stock prices.");
        } catch (Exception e) {
            logger.error("Error updating stock prices: ", e);
        }
    }
}
