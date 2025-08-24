package com.wallstreet.stock.market.simulation.service.kafkaconfig;

import com.wallstreet.stock.market.simulation.dto.OrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service responsible for producing (sending) messages to a Kafka topic.
 * This service is specifically designed to send order requests.
 */
@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    /**
     * The KafkaTemplate is Spring's high-level abstraction for sending messages.
     * It is thread-safe and configured automatically based on your application.properties.
     * The template is generic, specifying the types for the message key (String for the stock symbol)
     * and the message value (OrderDTO for the order details).
     */
    private final KafkaTemplate<String, OrderDTO> kafkaTemplate;

    /**
     * The name of the topic to which the messages will be sent.
     * This value is injected directly from the application.properties file,
     * which avoids hard-coding and makes the configuration flexible.
     */
    private final String topicName;

    /**
     * Constructs the KafkaProducerService with its required dependencies.
     * Spring's dependency injection will automatically provide the configured KafkaTemplate
     * and the topic name value.
     *
     * @param kafkaTemplate the pre-configured template for sending Kafka messages.
     * @param topicName     the topic name injected from the 'spring.kafka.topic.name' property.
     */
    @Autowired
    public KafkaProducerService(KafkaTemplate<String, OrderDTO> kafkaTemplate,
                                @Value("${spring.kafka.topic.name}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    /**
     * Sends an OrderDTO object to the configured Kafka topic.
     *
     * @param order The order data transfer object to be sent.
     */
    public void sendOrder(OrderDTO order) {
        try {
            logger.info("Sending order to Kafka topic '{}': {}", topicName, order.getId());

            // The send method is asynchronous. It returns a CompletableFuture if you need to handle the result.
            // Here, we use the stock symbol as the message key. This is a crucial design choice.
            // Kafka guarantees that all messages with the same key will be sent to the same partition.
            // This ensures that all orders for a single stock (e.g., "AAPL") are processed sequentially
            // in the order they were sent, preventing race conditions in the matching engine.
            kafkaTemplate.send(topicName, order.getSymbol(), order);

            logger.info("Successfully sent order: {}", order.getId());
        } catch (Exception e) {
            logger.error("Error sending order {} to Kafka: {}", order.getId(), e.getMessage());
            // Depending on requirements, you might want to re-throw this as a custom exception.
        }
    }
}
