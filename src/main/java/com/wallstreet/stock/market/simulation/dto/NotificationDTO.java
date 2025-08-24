package com.wallstreet.stock.market.simulation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

import com.wallstreet.stock.market.simulation.model.enums.NotificationType;

/**
 * Data Transfer Object for sending notifications to users.
 */
@Getter
@Builder
@ToString
public class NotificationDTO {

    /**
     * The ID of the user who should receive the notification.
     */
    private final UUID userId;

    /**
     * The type of notification to be sent.
     */
    private final NotificationType type;

    /**
     * The main content or message of the notification.
     */
    private final String message;

    /**
     * The timestamp when the notification was generated.
     */
    private final Instant timestamp;
}
