package com.example.OrderApiUpdate.repository;

import com.example.OrderApiUpdate.models.entity.PedidoItemLeitura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PedidoItemLeituraRepository
        extends JpaRepository<PedidoItemLeitura, Long> {

    List<PedidoItemLeitura> findByPedidoUuid(UUID pedidoUuid);

    void deleteByPedidoUuid(UUID pedidoUuid);
}
