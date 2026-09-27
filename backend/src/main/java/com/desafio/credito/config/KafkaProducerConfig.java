package com.desafio.credito.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaProducerConfig {

    @Value("${app.kafka.topic-auditoria:credito-consulta-auditoria}")
    private String topicAuditoria;

    @Bean
    public NewTopic auditoriaTopic() {
        return TopicBuilder.name(topicAuditoria)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
