package com.example.OrderApiRead.repository;

import com.example.OrderApiRead.models.entity.PedidoItemLeitura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PedidoItemLeituraRepository
        extends JpaRepository<PedidoItemLeitura, Long> {

    List<PedidoItemLeitura> findByPedidoUuid(UUID pedidoUuid);

    List<PedidoItemLeitura> findByPedidoUuidIn(
            List<UUID> pedidoUuids
    );
}
