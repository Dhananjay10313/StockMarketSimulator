package com.wallstreet.stock.market.simulation.service.orderprocessingservice;

import com.wallstreet.stock.market.simulation.model.Notification;
import com.wallstreet.stock.market.simulation.model.enums.NotificationType;
import com.wallstreet.stock.market.simulation.model.enums.OrderSide;
import com.wallstreet.stock.market.simulation.dto.OrderDTO;
import com.wallstreet.stock.market.simulation.repository.NotificationRepository;
import com.wallstreet.stock.market.simulation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public void createNotificationForOrder(OrderDTO orderDto) {
        Notification notification = new Notification();
        notification.setUser(userRepository.findById(orderDto.getUserId()).orElse(null));
        NotificationType notificationType = mapOrderToNotificationType(orderDto);

        notification.setType(notificationType); // Assuming a generic 'ORDER_UPDATE' type

        // Create a payload with relevant details for the frontend
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", orderDto.getId().toString());
        payload.put("symbol", orderDto.getSymbol());
        payload.put("status", orderDto.getStatus().name());
        payload.put("message", generateMessage(orderDto));

        notification.setPayload(generateMessage(orderDto));
        notification.setIsRead(false);

        notificationRepository.save(notification);
    }

    private String generateMessage(OrderDTO orderDto) {
        return switch (orderDto.getStatus()) {
            case FILLED -> String.format("Your %s order for %d shares of %s has been fully executed.",
                    orderDto.getSide(), orderDto.getQuantity(), orderDto.getSymbol());
            case PARTIAL -> String.format("Your %s order for %s has been partially executed.",
                    orderDto.getSide(), orderDto.getSymbol());
            case EXPIRED -> String.format("Your %s order for %s has expired.",
                    orderDto.getSide(), orderDto.getSymbol());
            case CANCELLED -> String.format("Your %s order for %s has been cancelled.",
                    orderDto.getSide(), orderDto.getSymbol());
            default -> String.format("Update on your %s order for %s. Status: %s",
                    orderDto.getSide(), orderDto.getSymbol(), orderDto.getStatus());
        };
    }

    private NotificationType mapOrderToNotificationType(OrderDTO orderDto) {
        return switch (orderDto.getStatus()) {
            case FILLED -> orderDto.getSide() == OrderSide.BUY ? NotificationType.TRADE_EXECUTED_BUY : NotificationType.TRADE_EXECUTED_SELL;
            case PARTIAL -> NotificationType.PARTIAL_TRADE_EXECUTED;
            case EXPIRED -> NotificationType.ORDER_EXPIRED;
            case CANCELLED, REJECTED -> NotificationType.ORDER_CANCELLED; // Grouping REJECTED with CANCELLED as there's no specific type.
            default -> NotificationType.ADMIN_MESSAGE; // Fallback for any other state.
        };
    }
}
