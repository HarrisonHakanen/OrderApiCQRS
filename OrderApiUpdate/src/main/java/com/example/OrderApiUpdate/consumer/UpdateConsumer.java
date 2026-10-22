package com.example.OrderApiUpdate.consumer;


import com.example.OrderApiUpdate.handler.OutboxEventHandlerRegistry;
import com.example.OrderApiUpdate.models.dto.PedidoCanceladoEvent;
import com.example.OrderApiUpdate.models.dto.PedidoCriadoEvent;
import com.example.OrderApiUpdate.service.PedidoReadModelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UpdateConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(UpdateConsumer.class);

    private final ObjectMapper objectMapper;
    private final PedidoReadModelService pedidoReadModelService;
    private final OutboxEventHandlerRegistry registry;

    public UpdateConsumer(ObjectMapper objectMapper,
                          PedidoReadModelService pedidoReadModelService,
                          OutboxEventHandlerRegistry registry) {
        this.objectMapper = objectMapper;
        this.pedidoReadModelService = pedidoReadModelService;
        this.registry = registry;
    }

    @KafkaListener(
            topics = "outbox.event.PEDIDO",
            groupId = "orders-update-group"
    )
    public void consumirPedido(String payload)
            throws JsonProcessingException {

        JsonNode json = objectMapper.readTree(payload);

        String tipoEvento = json.path("evento").asText();

        registry.buscar(tipoEvento).processar(json);
    }
}
