package com.wallstreet.stock.market.simulation.service.orderprocessingservice;

/**
 * Defines the contract for a specialist order processor.
 * Each implementation is responsible for processing a specific type of order,
 * including checking for and handling expired orders, and generating the resulting
 * actions like trades and notifications.
 */
@FunctionalInterface
public interface OrderProcessor {

    /**
     * Processes a set of orders based on the current market context.
     * This method should:
     * 1. Identify orders relevant to the processor's type (e.g., GTT, Limit).
     * 2. For each relevant order, first check if it has expired using the timestamp from the context. If so, handle its removal.
     * 3. If not expired, attempt to execute the order logic (e.g., trigger GTT, match Limit).
     * 4. Collate all resulting actions (trades, notifications, etc.) into a ProcessingResult object.
     *
     * @param context The shared context for the current processing cycle, containing market data and service dependencies.
     * @return A ProcessingResult object containing all the consequential actions to be executed by the orchestrator.
     */
    ProcessingResult process(ProcessingContext context);
}
