package com.example.OrderApiRead.models.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RelatorioPedidosResponse(
        LocalDate dataInicial,
        LocalDate dataFinal,
        long totalPedidos,
        long pedidosCancelados,
        long pedidosValidos,
        BigDecimal valorTotalVendas,
        BigDecimal ticketMedio,
        List<ResumoStatusPedido> resumoPorStatus
) {
    public record ResumoStatusPedido(
            String status,
            long quantidade,
            BigDecimal valorTotal
    ) {}
}
