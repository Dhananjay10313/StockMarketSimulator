package com.wallstreet.stock.market.simulation.controller;

import com.wallstreet.stock.market.simulation.dto.OrderDTO;
import com.wallstreet.stock.market.simulation.model.Order;
import com.wallstreet.stock.market.simulation.service.kafkaconfig.KafkaProducerService;
import com.wallstreet.stock.market.simulation.service.utils.OrderPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
// For a real app, you would use Spring Security to get the user
// import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final KafkaProducerService kafkaProducerService;
    private final OrderPersistenceService orderPersistenceService;

    @Autowired
    public OrderController(KafkaProducerService kafkaProducerService,
                           OrderPersistenceService orderPersistenceService) {
        this.kafkaProducerService = kafkaProducerService;
        this.orderPersistenceService = orderPersistenceService;
    }

    @PostMapping
    public ResponseEntity<String> submitOrder(@RequestBody OrderDTO orderDTO) {
        // In a real application, the user ID would come from the security context,
        // not the request body, to ensure a user cannot place an order for someone else.
        // For example: UUID userId = ((AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        UUID userId = orderDTO.getUserId(); // Using DTO field for this example
        if (userId == null) {
            return ResponseEntity.badRequest().body("User ID is required.");
        }

        try {
            logger.info("Received order request for user {}", userId);

            // Step 1: Persist the order to the database FIRST.
            // This is critical. It ensures we have a durable record of the order
            // before we attempt any asynchronous processing.
            Order savedOrder = orderPersistenceService.createAndPersistOrder(orderDTO, userId);
            logger.info("Successfully persisted order with new ID: {}", savedOrder.getId());

            // Step 2: Enrich the DTO with the permanent ID from the database.
            // This ensures the message sent to Kafka has the same ID as the database record.
            orderDTO.setId(savedOrder.getId());
            orderDTO.setCreatedAt(savedOrder.getCreatedAt().toInstant());

            // Step 3: Send the enriched DTO to Kafka for processing.
            kafkaProducerService.sendOrder(orderDTO);

            return ResponseEntity.status(HttpStatus.ACCEPTED)
                                 .body("Order accepted with ID: " + savedOrder.getId());

        } 
        // catch (Exception e) {
        //     logger.warn("Order submission failed: {}", e.getMessage());
        //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        // } 
        catch (Exception e) {
            logger.error("An unexpected error occurred while submitting order for user {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error occurred.");
        }
    }
}
