package com.example.OrderApiRead.models.dto;

import java.time.Instant;

public record PedidoStatusHistoricoResponse(
        String status,
        Instant data,
        String observacao
) {
}
