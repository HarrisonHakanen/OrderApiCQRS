package com.example.OrderApiUpdate.models.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PedidoCriadoEvent(
        String evento,
        UUID pedidoUuid,
        UUID clienteUuid,
        String status,
        BigDecimal subtotal,
        BigDecimal desconto,
        BigDecimal total,
        Instant criadoEm,
        List<ItemPedidoEvent> itens
) {
    public record ItemPedidoEvent(
            UUID produtoUuid,
            Integer quantidade,
            BigDecimal precoUnitario,
            BigDecimal desconto,
            BigDecimal subtotal
    ) {}
}
