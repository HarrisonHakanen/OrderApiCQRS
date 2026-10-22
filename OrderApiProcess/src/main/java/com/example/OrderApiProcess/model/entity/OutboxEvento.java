package com.example.OrderApiProcess.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "outbox_eventos",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_outbox_eventos_uuid",
                        columnNames = "uuid"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class OutboxEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "uuid",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private UUID uuid;

    @Column(name = "agregado_tipo", nullable = false, length = 100)
    private String agregadoTipo;

    @Column(name = "agregado_id", nullable = false)
    private Long agregadoId;

    @Column(name = "tipo_evento", nullable = false, length = 100)
    private String tipoEvento;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private Instant createdAt;

    @Column(name = "publicado_em")
    private Instant publicadoEm;

    @Column(name = "tentativas", nullable = false, insertable = false)
    private Integer tentativas;

    @Column(
            name = "proxima_tentativa_em",
            nullable = false,
            insertable = false
    )
    private Instant proximaTentativaEm;

    @Column(name = "ultimo_erro", columnDefinition = "TEXT")
    private String ultimoErro;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "CRIADO";
}