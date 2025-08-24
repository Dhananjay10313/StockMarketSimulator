package com.wallstreet.stock.market.simulation.service.orderprocessingservice;

import com.wallstreet.stock.market.simulation.service.orderprocessingservice.ProcessingResult;
import com.wallstreet.stock.market.simulation.model.Trade;
import com.wallstreet.stock.market.simulation.dto.OrderDTO;
import com.wallstreet.stock.market.simulation.dto.TradeDTO;
import com.wallstreet.stock.market.simulation.repository.OrderRepository;
import com.wallstreet.stock.market.simulation.repository.TradeRepository;
import com.wallstreet.stock.market.simulation.service.orderprocessingservice.NotificationService;
import com.wallstreet.stock.market.simulation.service.orderprocessingservice.PortfolioService;
import com.wallstreet.stock.market.simulation.service.orderprocessingservice.PostOrderProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostOrderProcessingServiceImpl implements PostOrderProcessingService {

    private final TradeRepository tradeRepository;
    private final OrderRepository orderRepository;
    private final PortfolioService portfolioService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void processResults(List<ProcessingResult> results) {
        for (ProcessingResult result : results) {
            // Step 1: Persist all newly executed trades and update portfolios
            if (result.getExecutedTrades() != null && !result.getExecutedTrades().isEmpty()) {
                // Convert DTOs to JPA entities for persistence
                List<Trade> tradesToSave = result.getExecutedTrades().stream()
                        .map(this::convertToTradeEntity)
                        .toList();
                tradeRepository.saveAll(tradesToSave);

                // For each saved trade, update the portfolios of the buyer and seller
                tradesToSave.forEach(portfolioService::updatePortfolioAfterTrade);
            }

            // Step 2: Update the status of all processed orders (e.g., FILLED, PARTIAL, EXPIRED)
            if (result.getProcessedOrders() != null && !result.getProcessedOrders().isEmpty()) {
                for (OrderDTO orderDto : result.getProcessedOrders()) {
                    // Update the order's status in the database
                    orderRepository.updateOrderStatus(orderDto.getId(), orderDto.getStatus());

                    // Step 3: Create a notification for the final state of the order
                    notificationService.createNotificationForOrder(orderDto);
                }
            }
        }
    }

    private Trade convertToTradeEntity(TradeDTO tradeDto) {
        // This helper method converts the TradeDTO from the processing logic
        // into a persistable JPA Trade entity.
        Trade trade = new Trade();
        trade.setSymbol(tradeDto.getSymbol());
        trade.setPrice(BigDecimal.valueOf(tradeDto.getPrice()));
        trade.setQty(tradeDto.getQuantity());
        trade.setTimestamp(tradeDto.getTimestamp().atOffset(ZoneOffset.UTC));
        trade.setBuyOrderId(tradeDto.getBuyOrderId());
        trade.setSellOrderId(tradeDto.getSellOrderId());


        return trade;
    }
}
