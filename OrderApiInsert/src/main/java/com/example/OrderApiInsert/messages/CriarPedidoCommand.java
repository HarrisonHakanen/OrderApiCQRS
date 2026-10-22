package com.example.OrderApiInsert.messages;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CriarPedidoCommand(
        UUID commandUUID,
        UUID orderUUID,
        UUID clienteUuid,
        List<Item> itens,
        Instant solicitadoEm
) {
    public record Item(
            UUID produtoUuid,
            Integer quantidade
    ) {}
}
