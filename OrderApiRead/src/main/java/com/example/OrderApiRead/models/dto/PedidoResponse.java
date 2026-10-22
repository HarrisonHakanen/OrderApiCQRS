package com.example.OrderApiRead.models.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PedidoResponse(
        UUID pedidoUuid,
        UUID clienteUuid,
        String status,
        BigDecimal subtotal,
        BigDecimal desconto,
        BigDecimal total,
        Instant criadoEm,
        Instant atualizadoEm,
        List<PedidoItemResponse> itens
) {}
