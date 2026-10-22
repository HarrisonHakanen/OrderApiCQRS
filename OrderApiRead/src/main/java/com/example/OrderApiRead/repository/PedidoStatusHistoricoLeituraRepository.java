package com.example.OrderApiRead.repository;

import com.example.OrderApiRead.models.entity.PedidoStatusHistoricoLeitura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PedidoStatusHistoricoLeituraRepository
        extends JpaRepository<PedidoStatusHistoricoLeitura, Long> {

    List<PedidoStatusHistoricoLeitura>
    findByPedidoUuidOrderByDataStatusAscIdAsc(UUID pedidoUuid);

    boolean existsByPedidoUuid(UUID pedidoUuid);
}
