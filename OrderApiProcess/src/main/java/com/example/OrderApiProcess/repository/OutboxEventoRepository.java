package com.example.OrderApiProcess.repository;

import com.example.OrderApiProcess.model.entity.OutboxEvento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventoRepository
        extends JpaRepository<OutboxEvento, Long> {
}