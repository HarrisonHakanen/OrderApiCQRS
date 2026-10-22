package com.example.OrderApiRead.projections;

import java.math.BigDecimal;

public interface RelatorioPedidosStatusProjection {
    String getStatus();
    Long getQuantidade();
    BigDecimal getValorTotal();
}
