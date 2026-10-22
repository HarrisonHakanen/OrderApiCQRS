package com.example.OrderApiProcess.model.entity;

import com.example.OrderApiProcess.model.enums.PedidoStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pedido_status_historicos")
@Getter
@Setter
@NoArgsConstructor
public class PedidoStatusHistorico {

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_anterior", length = 20)
    private PedidoStatus statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_novo", nullable = false, length = 20)
    private PedidoStatus statusNovo;

    @CreationTimestamp
    @Column(name = "data_alteracao", nullable = false, updatable = false)
    private Instant dataAlteracao;

    @Column(length = 255)
    private String motivo;
}
