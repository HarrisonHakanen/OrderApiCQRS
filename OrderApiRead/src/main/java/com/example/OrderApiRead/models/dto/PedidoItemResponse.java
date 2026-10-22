package com.example.OrderApiRead.models.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PedidoItemResponse(
        UUID produtoUuid,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal desconto,
        BigDecimal subtotal
) {}
