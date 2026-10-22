package com.example.OrderApiInsert.dto;

import java.time.Instant;
import java.util.UUID;

public record AlterarStatusPedidoCommand(
        UUID commandUUID,
        UUID orderUUID,
        String statusSolicitado,
        String motivo,
        Instant solicitadoEm
) {}
