package com.wallstreet.stock.market.simulation.service.orderprocessingservice;

import com.wallstreet.stock.market.simulation.service.OrderBookManager;
import com.wallstreet.stock.market.simulation.service.influxservice.InfluxService;
import com.wallstreet.stock.market.simulation.service.ltp.LtpService;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

/**
 * A container for all data required during a single order processing cycle.
 * An instance of this class is created by the GlobalOrderProcessingService
 * and passed to each OrderProcessor.
 */
@Getter
@AllArgsConstructor
public class ProcessingContext {

    /**
     * The timestamp marking the beginning of this processing cycle. Used for
     * evaluating order expirations.
     */
    private final Instant cycleTimestamp;

    /**
     * A snapshot of the Last Traded Prices for all stocks at the start of the cycle.
     * Key: Stock Symbol, Value: LTP.
     */
    private final Map<String, Double> allLtps;

    /**
     * A reference to the central order book manager to access order books.
     */
    private final OrderBookManager orderBookManager;
    
    /**
     * A reference to the LTP service for any potential real-time lookups if needed.
     */
    private final LtpService ltpService;

    /**
     * A reference to the influx service for any potential real-time lookups if needed.
     */
    private final InfluxService influxService;

    // You can add other shared services or data here as needed, such as
    // references to a NotificationService or a WalletService if you choose
    // a different architectural pattern later.
}
