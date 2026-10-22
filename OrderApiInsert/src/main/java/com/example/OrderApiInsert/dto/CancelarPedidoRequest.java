package com.example.OrderApiInsert.dto;

import jakarta.validation.constraints.Size;

public record CancelarPedidoRequest(
        @Size(max = 255) String motivo
) {}
