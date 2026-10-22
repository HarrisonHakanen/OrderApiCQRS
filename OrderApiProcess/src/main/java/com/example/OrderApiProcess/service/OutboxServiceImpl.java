package com.example.OrderApiProcess.service;

import com.example.OrderApiProcess.model.entity.OutboxEvento;
import com.example.OrderApiProcess.model.entity.Pedido;
import com.example.OrderApiProcess.model.entity.PedidoItem;
import com.example.OrderApiProcess.repository.OutboxEventoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class OutboxServiceImpl implements OutboxService {

    private final OutboxEventoRepository outboxEventoRepository;
    private final ObjectMapper objectMapper;

    public OutboxServiceImpl(
            OutboxEventoRepository outboxEventoRepository,
            ObjectMapper objectMapper) {
        this.outboxEventoRepository = outboxEventoRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void registrarPedidoCriado(
            Pedido pedido,
            List<PedidoItem> itens) {

        Map<String, Object> payload = Map.of(
                "evento", "PEDIDO_CRIADO",
                "pedidoUuid", pedido.getUuid(),
                "clienteUuid",pedido.getCliente().getUuid(),
                "criadoEm",pedido.getCreatedAt(),
                "status", pedido.getStatus(),
                "subtotal", pedido.getSubtotal(),
                "desconto", pedido.getDesconto(),
                "total", pedido.getTotal(),
                "itens", itens.stream()
                        .map(item -> Map.of(
                                "produtoUuid",
                                item.getProduto().getUuid(),
                                "quantidade",
                                item.getQuantidade(),
                                "precoUnitario",
                                item.getPrecoUnitario(),
                                "desconto",
                                item.getDesconto(),
                                "subtotal",
                                item.getSubtotal()
                        ))
                        .toList()
        );

        final String payloadJson;

        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Não foi possível serializar o evento da outbox.",
                    e
            );
        }

        OutboxEvento evento = new OutboxEvento();

        evento.setAgregadoTipo("PEDIDO");
        evento.setAgregadoId(pedido.getId());
        evento.setTipoEvento("PEDIDO_CRIADO");
        evento.setPayload(payloadJson);
        evento.setStatus("CRIADO");

        outboxEventoRepository.save(evento);
    }


    @Override
    @Transactional
    public void cancelarPedido(
            Pedido pedido) {

        Map<String, Object> payload = Map.of(
                "evento", "PEDIDO_CANCELADO",
                "pedidoUuid", pedido.getUuid(),
                "clienteUuid",pedido.getCliente().getUuid(),
                "criadoEm",pedido.getCreatedAt(),
                "status", pedido.getStatus(),
                "subtotal", pedido.getSubtotal(),
                "desconto", pedido.getDesconto(),
                "total", pedido.getTotal()
        );

        final String payloadJson;

        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Não foi possível serializar o evento da outbox.",
                    e
            );
        }

        OutboxEvento evento = new OutboxEvento();

        evento.setAgregadoTipo("PEDIDO");
        evento.setAgregadoId(pedido.getId());
        evento.setTipoEvento("PEDIDO_CANCELADO");
        evento.setPayload(payloadJson);
        evento.setStatus("CRIADO");

        outboxEventoRepository.save(evento);
    }

    @Override
    @Transactional
    public void alterarPedido(
            Pedido pedido) {

        Map<String, Object> payload = Map.of(
                "evento", "PEDIDO_ALTERADO",
                "pedidoUuid", pedido.getUuid(),
                "clienteUuid",pedido.getCliente().getUuid(),
                "criadoEm",pedido.getCreatedAt(),
                "status", pedido.getStatus(),
                "subtotal", pedido.getSubtotal(),
                "desconto", pedido.getDesconto(),
                "total", pedido.getTotal()
        );

        final String payloadJson;

        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Não foi possível serializar o evento da outbox.",
                    e
            );
        }

        OutboxEvento evento = new OutboxEvento();

        evento.setAgregadoTipo("PEDIDO");
        evento.setAgregadoId(pedido.getId());
        evento.setTipoEvento("PEDIDO_ALTERADO");
        evento.setPayload(payloadJson);
        evento.setStatus("CRIADO");

        outboxEventoRepository.save(evento);
    }
}
