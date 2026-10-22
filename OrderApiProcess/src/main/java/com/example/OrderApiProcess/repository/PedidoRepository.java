package com.example.OrderApiProcess.repository;

import com.example.OrderApiProcess.model.entity.Pedido;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    boolean existsByUuid(UUID uuid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Pedido> findByUuid(UUID uuid);
}
