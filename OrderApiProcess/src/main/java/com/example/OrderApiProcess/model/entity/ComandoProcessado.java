package com.example.OrderApiProcess.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "comandos_processados")
@Getter
@NoArgsConstructor
public class ComandoProcessado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "command_uuid", nullable = false, unique = true)
    private UUID commandUUID;

    @Column(name = "tipo_comando", nullable = false, length = 50)
    private String tipoComando;

    @Column(name = "pedido_uuid", nullable = false)
    private UUID pedidoUUID;

    @Column(name = "processado_em", nullable = false)
    private Instant processadoEm;

    public ComandoProcessado(
            UUID commandUUID,
            String tipoComando,
            UUID pedidoUUID
    ) {
        this.commandUUID = commandUUID;
        this.tipoComando = tipoComando;
        this.pedidoUUID = pedidoUUID;
        this.processadoEm = Instant.now();
    }
}
