package com.example.OrderApiInsert.dto;

import java.util.UUID;

public record ComandoAceitoResponse(
        UUID orderUUID,
        String status,
        String message
) {}
