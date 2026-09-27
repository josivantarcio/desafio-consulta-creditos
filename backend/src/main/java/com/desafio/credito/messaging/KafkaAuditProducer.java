package com.desafio.credito.messaging;

import com.desafio.credito.dto.CreditoConsultaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class KafkaAuditProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaAuditProducer.class);

    private final KafkaTemplate<String, CreditoConsultaEvent> kafkaTemplate;

    @Value("${app.kafka.enabled:true}")
    private boolean kafkaEnabled;

    @Value("${app.kafka.topic-auditoria:credito-consulta-auditoria}")
    private String topicAuditoria;

    @Autowired
    public KafkaAuditProducer(@Autowired(required = false) KafkaTemplate<String, CreditoConsultaEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarEventoAuditoria(String tipoConsulta, String parametro, int quantidadeResultados, String status) {
        if (!kafkaEnabled || kafkaTemplate == null) {
            log.info("Kafka desabilitado ou indisponível. Evento de auditoria ignorado (Tipo: {}, Param: {})", tipoConsulta, parametro);
            return;
        }

        CreditoConsultaEvent event = CreditoConsultaEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .tipoConsulta(tipoConsulta)
                .parametroConsulta(parametro)
                .quantidadeResultados(quantidadeResultados)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();

        try {
            kafkaTemplate.send(topicAuditoria, parametro, event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.warn("Falha assíncrona ao publicar evento de auditoria no Kafka: {}", ex.getMessage());
                        } else {
                            log.info("Evento de auditoria publicado com sucesso no tópico '{}' [offset: {}]",
                                    topicAuditoria, result.getRecordMetadata().offset());
                        }
                    });
        } catch (Exception e) {
            log.warn("Erro ao submeter evento de auditoria ao Kafka: {}. A requisição HTTP continuará normalmente.", e.getMessage());
        }
    }
}
