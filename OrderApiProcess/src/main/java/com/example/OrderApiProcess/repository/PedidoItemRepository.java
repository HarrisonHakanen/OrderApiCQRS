package com.example.OrderApiProcess.repository;

import com.example.OrderApiProcess.model.entity.PedidoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoItemRepository
        extends JpaRepository<PedidoItem, Long> {
}
