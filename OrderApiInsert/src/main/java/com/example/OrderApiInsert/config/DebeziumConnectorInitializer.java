package com.example.OrderApiInsert.config;

import com.example.OrderApiInsert.service.DebeziumConnectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DebeziumConnectorInitializer {

    private static final Logger log =
            LoggerFactory.getLogger(DebeziumConnectorInitializer.class);

    private final DebeziumConnectorService connectorService;

    public DebeziumConnectorInitializer(
            DebeziumConnectorService connectorService
    ) {
        this.connectorService = connectorService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void inicializar() {
        try {
            connectorService.registrarConector();
            log.info("Conector Outbox do Debezium registrado.");
        } catch (Exception e) {
            log.error(
                    "Não foi possível registrar o conector Outbox do Debezium.",
                    e
            );
        }
    }
}
