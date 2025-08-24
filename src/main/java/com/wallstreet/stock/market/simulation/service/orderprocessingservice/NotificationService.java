package com.wallstreet.stock.market.simulation.service.orderprocessingservice;

import com.wallstreet.stock.market.simulation.dto.OrderDTO;

public interface NotificationService {

    /**
     * Creates a notification for a user based on the final status of their order.
     *
     * @param orderDto The processed order DTO containing the final status and user info.
     */
    void createNotificationForOrder(OrderDTO orderDto);
}
