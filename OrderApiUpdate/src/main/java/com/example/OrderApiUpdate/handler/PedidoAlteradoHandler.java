package com.example.OrderApiUpdate.handler;

import com.example.OrderApiUpdate.models.dto.PedidoCanceladoEvent;
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
public class PedidoAlteradoHandler implements OutboxEventHandler {

    private final ObjectMapper objectMapper;
    private final PedidoReadModelService pedidoReadModelService;

    @Override
    public String tipoEvento() {
        return "PEDIDO_ALTERADO";
    }

    @Override
    public void processar(JsonNode payload)
            throws JsonProcessingException {

        PedidoCanceladoEvent evento =
                objectMapper.treeToValue(
                        payload,
                        PedidoCanceladoEvent.class
                );

        pedidoReadModelService.processarPedidoAlterado(evento);

        log.info(
                "Alteração recebida: uuid={}, status={}",
                evento.pedidoUuid(),
                evento.status()
        );
    }
}
