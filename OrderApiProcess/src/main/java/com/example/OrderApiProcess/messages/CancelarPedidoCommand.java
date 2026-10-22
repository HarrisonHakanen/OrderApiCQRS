package com.example.OrderApiProcess.messages;

import java.time.Instant;
import java.util.UUID;

public record CancelarPedidoCommand(
        UUID commandUUID,
        UUID orderUUID,
        String motivo,
        Instant solicitadoEm
) {}
