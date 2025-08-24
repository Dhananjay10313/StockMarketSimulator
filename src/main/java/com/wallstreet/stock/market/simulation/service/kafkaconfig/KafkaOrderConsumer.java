package com.wallstreet.stock.market.simulation.service.kafkaconfig;

import com.wallstreet.stock.market.simulation.dto.OrderDTO;
import com.wallstreet.stock.market.simulation.service.orderprocessingservice.GlobalOrderProcessingOrchestrator;
import com.wallstreet.stock.market.simulation.service.OrderBookManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaOrderConsumer {

    // ... logger and dependencies are the same ...
    private static final Logger logger = LoggerFactory.getLogger(KafkaOrderConsumer.class);
    private final OrderBookManager orderBookManager;
    private final GlobalOrderProcessingOrchestrator orchestrator;

    @Autowired
    public KafkaOrderConsumer(OrderBookManager orderBookManager,
                              GlobalOrderProcessingOrchestrator orchestrator) {
        this.orderBookManager = orderBookManager;
        this.orchestrator = orchestrator;
    }


    @KafkaListener(
        topics = "${spring.kafka.topic.name}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    // ADD THE 'synchronized' KEYWORD TO THE METHOD SIGNATURE
    public synchronized void consumeOrderRequest(OrderDTO order) {
        logger.info("Consumed order from Kafka, ID: [{}]", order.getId());

        try {
            // The logic inside the method remains exactly the same
            logger.debug("Adding order {} to the OrderBookManager.", order.getId());
            orderBookManager.addOrder(order);
            logger.info("Successfully added order {} to the in-memory book.", order.getId());

            logger.info("Triggering GlobalOrderProcessingOrchestrator for order {}.", order.getId());
            orchestrator.runProcessingCycle();
            logger.info("Finished processing cycle triggered by order {}.", order.getId());

        } catch (Exception e) {
            logger.error("Failed to process consumed order {}: {}", order.getId(), e.getMessage(), e);
        }
    }
}
