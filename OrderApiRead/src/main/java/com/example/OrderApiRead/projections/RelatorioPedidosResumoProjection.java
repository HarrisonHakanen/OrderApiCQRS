package com.example.OrderApiRead.projections;

import java.math.BigDecimal;

public interface RelatorioPedidosResumoProjection {
    Long getTotalPedidos();
    Long getPedidosCancelados();
    Long getPedidosValidos();
    BigDecimal getValorTotalVendas();
    BigDecimal getTicketMedio();
}
