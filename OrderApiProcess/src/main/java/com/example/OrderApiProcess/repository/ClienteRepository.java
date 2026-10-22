package com.example.OrderApiProcess.repository;

import com.example.OrderApiProcess.model.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByUuid(UUID uuid);
}
