package com.wallstreet.stock.market.simulation.service.orderprocessingservice;

import com.wallstreet.stock.market.simulation.model.Holding;
import com.wallstreet.stock.market.simulation.model.Trade;
import com.wallstreet.stock.market.simulation.model.User;
import com.wallstreet.stock.market.simulation.model.Wallet;
import com.wallstreet.stock.market.simulation.repository.HoldingRepository;
import com.wallstreet.stock.market.simulation.repository.UserRepository;
import com.wallstreet.stock.market.simulation.repository.WalletRepository;
import com.wallstreet.stock.market.simulation.service.influxservice.InfluxService;
import com.wallstreet.stock.market.simulation.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final WalletRepository walletRepository;
    private final HoldingRepository holdingRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository; 
    private final InfluxService influxService; // Assuming you have a way to get user from order

    @Override
    public void updatePortfolioAfterTrade(Trade trade) {
        // Assuming Trade entity has references or IDs to the buyer and seller orders,
        // from which we can retrieve the users.
        User buyer = orderRepository.findUserByOrderId(trade.getBuyOrderId()).orElseThrow();
        User seller = orderRepository.findUserByOrderId(trade.getSellOrderId()).orElseThrow();

        BigDecimal tradeValue = trade.getPrice().multiply(new BigDecimal(trade.getQty()));

        // Update wallets
        updateWallet(buyer.getId(), tradeValue.negate()); // Debit buyer
        updateWallet(seller.getId(), tradeValue);       // Credit seller

        // Update holdings
        updateHolding(buyer.getId(), trade.getSymbol(), trade.getQty(), trade.getPrice()); // Add stock to buyer
        updateHolding(seller.getId(), trade.getSymbol(), -trade.getQty(), null); // Remove stock from seller
    }

    private void updateWallet(UUID userId, BigDecimal amount) {
        Wallet wallet = walletRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Wallet not found for user: " + userId));
        wallet.setCashBalance(wallet.getCashBalance().add(amount));
        walletRepository.save(wallet);
    }

    private void updateHolding(UUID userId, String symbol, long quantityChange, BigDecimal newPrice) {
        Holding holding = holdingRepository.findByUserIdAndSymbol(userId, symbol)
                .orElseGet(() -> createNewHolding(userId, symbol));

        long newQuantity = holding.getQty() + quantityChange;

        if (newQuantity > 0) {
            // If buying, update the average price. If selling, avg price remains the same.
            if (quantityChange > 0) {
                BigDecimal oldTotalValue = holding.getAvgPrice().multiply(new BigDecimal(holding.getQty()));
                BigDecimal addedValue = newPrice.multiply(new BigDecimal(quantityChange));
                BigDecimal newTotalValue = oldTotalValue.add(addedValue);
                BigDecimal newAvgPrice = newTotalValue.divide(new BigDecimal(newQuantity), 2, BigDecimal.ROUND_HALF_UP);
                holding.setAvgPrice(newAvgPrice);
            }
            holding.setQty(newQuantity);
            holdingRepository.save(holding);
        } else {
            // If the quantity is zero or less, remove the holding from the portfolio
            holdingRepository.delete(holding);
        }
    }

    private Holding createNewHolding(UUID userId, String symbol) {
        Holding newHolding = new Holding();
        User user = userRepository.findById(userId).orElseThrow();
        newHolding.setUser(user);
        newHolding.setSymbol(symbol);
        newHolding.setQty(0L);
        newHolding.setAvgPrice(BigDecimal.ZERO);
        return newHolding;
    }
}
