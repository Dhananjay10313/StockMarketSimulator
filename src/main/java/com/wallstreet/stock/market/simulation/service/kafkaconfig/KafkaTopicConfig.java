package com.wallstreet.stock.market.simulation.service.kafkaconfig;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topic.name}")
    private String topicName;

    @Bean
    public NewTopic orderRequestsTopic() {
        return TopicBuilder.name(topicName)
                .partitions(1) // You can increase partitions for more parallelism later
                .replicas(1)
                .build();
    }
}
