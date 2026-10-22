package com.example.OrderApiUpdate.repository;

import com.example.OrderApiUpdate.models.entity.PedidoLeitura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PedidoLeituraRepository
        extends JpaRepository<PedidoLeitura, Long> {

    Optional<PedidoLeitura> findByPedidoUuid(UUID pedidoUuid);

    boolean existsByPedidoUuid(UUID pedidoUuid);
}
