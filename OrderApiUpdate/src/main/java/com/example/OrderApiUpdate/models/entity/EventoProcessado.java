package com.example.OrderApiUpdate.models.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "eventos_processados",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_eventos_processados_tipo_chave",
                columnNames = {"tipo_evento", "chave_idempotencia"}
        )
)
@Getter
@Setter
public class EventoProcessado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chave_idempotencia", nullable = false)
    private UUID chaveIdempotencia;

    @Column(name = "tipo_evento", nullable = false, length = 100)
    private String tipoEvento;

    @Column(name = "processado_em", nullable = false)
    private Instant processadoEm;


    public EventoProcessado(UUID chaveIdempotencia, String tipoEvento) {
        this.chaveIdempotencia = chaveIdempotencia;
        this.tipoEvento = tipoEvento;
        this.processadoEm = Instant.now();
    }
}
