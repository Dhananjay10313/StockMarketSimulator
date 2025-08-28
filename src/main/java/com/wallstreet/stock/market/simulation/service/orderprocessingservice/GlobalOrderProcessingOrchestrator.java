package com.wallstreet.stock.market.simulation.service.orderprocessingservice;

import com.wallstreet.stock.market.simulation.service.orderprocessingservice.ProcessingContext;
import com.wallstreet.stock.market.simulation.service.ltp.LtpService;
import com.wallstreet.stock.market.simulation.service.OrderBookManager;
import com.wallstreet.stock.market.simulation.service.influxservice.InfluxService;
import com.wallstreet.stock.market.simulation.service.orderprocessingservice.ProcessingResult;
import com.wallstreet.stock.market.simulation.service.orderprocessingservice.OrderProcessor;
import com.wallstreet.stock.market.simulation.service.orderprocessingservice.PostOrderProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The central orchestrator for the entire order matching engine.
 * This service runs on a schedule, initiates the processing cycle,
 * invokes all available order processors, and delegates the results
 * for persistence and notification.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GlobalOrderProcessingOrchestrator {

    // Spring automatically injects all beans that implement the OrderProcessor interface.
    private final List<OrderProcessor> orderProcessors;
    private final PostOrderProcessingService postOrderProcessingService;
    private final LtpService ltpService;
    private final OrderBookManager orderBookManager;
    private final InfluxService influxService;

    /**
     * The main processing loop, scheduled to run at a fixed delay.
     * The fixedDelay ensures that a new cycle will not start until 5 seconds
     * after the previous cycle has completely finished.
     */
    public void runProcessingCycle() {
        log.info("Starting new order processing cycle at {}", Instant.now());

        // 1. Initialize the Processing Context for this specific cycle.
        // This provides a consistent snapshot of market data to all processors.
        Map<String, Double> ltpSnapshot = ltpService.getAllLtps();
        ProcessingContext context = new ProcessingContext(
            Instant.now(),
            ltpSnapshot,
            orderBookManager,
            ltpService,
            influxService
        );

        List<ProcessingResult> allResults = new ArrayList<>();

        // 2. Run each Order Processor sequentially.
        // The order of execution can be controlled using Spring's @Order annotation on the processor classes.
        log.info("Executing {} registered order processor(s).", orderProcessors.size());
        for (OrderProcessor processor : orderProcessors) {
            try {
                log.debug("Running processor: {}", processor.getClass().getSimpleName());
                ProcessingResult result = processor.process(context);
                
                // Only add the result if it contains meaningful data to avoid unnecessary work later.
                if (result != null && (!result.getExecutedTrades().isEmpty() || !result.getProcessedOrders().isEmpty())) {
                    allResults.add(result);
                    log.debug("Processor {} generated {} trades and {} order updates.",
                            processor.getClass().getSimpleName(),
                            result.getExecutedTrades().size(),
                            result.getProcessedOrders().size());
                }
            } catch (Exception e) {
                log.error("Error executing processor: {}", processor.getClass().getSimpleName(), e);
                // For robustness, we log the error and continue to the next processor.
            }
        }

        // 3. Call the PostOrderProcessor with the aggregated results.
        // This step will handle all database transactions in an atomic way.
        if (!allResults.isEmpty()) {
            log.info("Processing cycle generated results. Handing off to PostOrderProcessingService.");
            try {
                postOrderProcessingService.processResults(allResults);
            } catch (Exception e) {
                // This is a critical failure. The @Transactional annotation on the post-processing
                // service should have rolled back changes, but we must log this severe error.
                log.error("CRITICAL: A failure occurred during the post-processing and commit phase.", e);
            }
        } else {
            log.info("No new trades or order updates were generated in this cycle.");
        }

        log.info("Finished order processing cycle.");
    }

}
