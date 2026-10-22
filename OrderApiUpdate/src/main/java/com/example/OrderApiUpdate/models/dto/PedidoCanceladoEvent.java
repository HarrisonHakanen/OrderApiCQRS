package com.example.OrderApiUpdate.models.dto;

import com.example.OrderApiUpdate.models.enums.PedidoStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PedidoCanceladoEvent(
        String evento,
        UUID pedidoUuid,
        UUID clienteUuid,
        Instant criadoEm,
        PedidoStatus status,
        BigDecimal subtotal,
        BigDecimal desconto,
        BigDecimal total
) {}
