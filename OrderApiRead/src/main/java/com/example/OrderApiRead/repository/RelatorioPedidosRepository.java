package com.example.OrderApiRead.repository;

import com.example.OrderApiRead.models.entity.PedidoLeitura;
import com.example.OrderApiRead.projections.RelatorioPedidosResumoProjection;
import com.example.OrderApiRead.projections.RelatorioPedidosStatusProjection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface RelatorioPedidosRepository
        extends JpaRepository<PedidoLeitura, Long> {

    @Query(value = """
        SELECT
            COUNT(*) AS "totalPedidos",
            COUNT(*) FILTER (
                WHERE status = 'CANCELADO'
            ) AS "pedidosCancelados",
            COUNT(*) FILTER (
                WHERE status <> 'CANCELADO'
            ) AS "pedidosValidos",
            COALESCE(
                SUM(total) FILTER (
                    WHERE status <> 'CANCELADO'
                ),
                0
            ) AS "valorTotalVendas",
            COALESCE(
                AVG(total) FILTER (
                    WHERE status <> 'CANCELADO'
                ),
                0
            ) AS "ticketMedio"
        FROM pedidos_leitura
        WHERE criado_em >= :inicio
          AND criado_em < :fim
        """, nativeQuery = true)
    RelatorioPedidosResumoProjection buscarResumo(
            @Param("inicio") Instant inicio,
            @Param("fim") Instant fim
    );

    @Query(value = """
        SELECT
            status AS "status",
            COUNT(*) AS "quantidade",
            COALESCE(SUM(total), 0) AS "valorTotal"
        FROM pedidos_leitura
        WHERE criado_em >= :inicio
          AND criado_em < :fim
        GROUP BY status
        ORDER BY status
        """, nativeQuery = true)
    List<RelatorioPedidosStatusProjection> buscarResumoPorStatus(
            @Param("inicio") Instant inicio,
            @Param("fim") Instant fim
    );
}
