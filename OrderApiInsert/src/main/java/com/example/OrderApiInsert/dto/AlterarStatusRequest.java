package com.example.OrderApiInsert.dto;

import com.example.OrderApiInsert.models.enums.PedidoStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlterarStatusRequest(
        @NotNull PedidoStatus status,
        @Size(max = 255) String motivo
) {}
