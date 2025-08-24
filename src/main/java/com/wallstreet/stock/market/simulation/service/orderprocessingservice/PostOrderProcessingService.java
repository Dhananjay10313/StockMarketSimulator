package com.wallstreet.stock.market.simulation.service.orderprocessingservice;

import com.wallstreet.stock.market.simulation.service.orderprocessingservice.ProcessingResult;
import java.util.List;

public interface PostOrderProcessingService {

    /**
     * Processes a list of results from the order processing cycle. This method is the single
     * point of entry for persisting all changes, including executed trades, updated order
     * statuses, user portfolio changes (wallets and holdings), and notifications.
     *
     * @param results A list containing ProcessingResult objects from the various order processors.
     */
    void processResults(List<ProcessingResult> results);
}
