package com.example.OrderApiUpdate.repository;

import com.example.OrderApiUpdate.models.entity.EventoProcessado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventoProcessadoRepository
        extends JpaRepository<EventoProcessado, Long> {

    boolean existsByTipoEventoAndChaveIdempotencia(
            String tipoEvento,
            UUID chaveIdempotencia
    );
}
