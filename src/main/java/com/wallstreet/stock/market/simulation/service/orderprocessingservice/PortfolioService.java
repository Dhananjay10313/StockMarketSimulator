package com.wallstreet.stock.market.simulation.service.orderprocessingservice;

import com.wallstreet.stock.market.simulation.model.Trade;

public interface PortfolioService {

    /**
     * Updates the wallets and holdings for both the buyer and seller involved in a trade.
     * This method orchestrates the entire portfolio update for a single transaction.
     *
     * @param trade The executed trade containing buyer, seller, price, and quantity info.
     */
    void updatePortfolioAfterTrade(Trade trade);
}
