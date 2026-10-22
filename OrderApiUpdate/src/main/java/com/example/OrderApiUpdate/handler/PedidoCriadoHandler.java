package com.example.OrderApiUpdate.handler;

import com.example.OrderApiUpdate.models.dto.PedidoCriadoEvent;
import com.example.OrderApiUpdate.service.PedidoReadModelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoCriadoHandler implements OutboxEventHandler {

    private final ObjectMapper objectMapper;
    private final PedidoReadModelService pedidoReadModelService;

    @Override
    public String tipoEvento() {
        return "PEDIDO_CRIADO";
    }

    @Override
    public void processar(JsonNode payload)
            throws JsonProcessingException {

        PedidoCriadoEvent evento =
                objectMapper.treeToValue(
                        payload,
                        PedidoCriadoEvent.class
                );

        pedidoReadModelService.processarPedidoCriado(evento);

        log.info(
                "Pedido recebido: uuid={}",
                evento.pedidoUuid()
        );
    }
}
