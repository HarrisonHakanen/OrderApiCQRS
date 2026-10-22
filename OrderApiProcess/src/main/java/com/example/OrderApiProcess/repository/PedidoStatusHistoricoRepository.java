package com.example.OrderApiProcess.repository;

import com.example.OrderApiProcess.model.entity.PedidoStatusHistorico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoStatusHistoricoRepository
        extends JpaRepository<PedidoStatusHistorico, Long> {
}
