package com.example.OrderApiInsert.repository;

import com.example.OrderApiInsert.models.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByUuid(UUID uuid);
}
