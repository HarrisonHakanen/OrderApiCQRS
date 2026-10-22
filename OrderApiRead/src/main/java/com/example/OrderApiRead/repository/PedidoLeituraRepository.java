package com.example.OrderApiRead.repository;

import com.example.OrderApiRead.models.entity.PedidoLeitura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PedidoLeituraRepository
        extends JpaRepository<PedidoLeitura, Long>,
        JpaSpecificationExecutor<PedidoLeitura> {

    java.util.Optional<PedidoLeitura> findByPedidoUuid(UUID pedidoUuid);
}