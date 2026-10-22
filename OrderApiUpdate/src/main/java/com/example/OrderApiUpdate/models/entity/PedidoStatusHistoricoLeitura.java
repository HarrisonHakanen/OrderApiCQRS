package com.example.OrderApiUpdate.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pedido_status_historicos_leitura")
@NoArgsConstructor
@Getter
@Setter
public class PedidoStatusHistoricoLeitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pedido_uuid", nullable = false)
    private UUID pedidoUuid;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "data_status", nullable = false)
    private Instant dataStatus;

    @Column(name = "observacao", length = 500)
    private String observacao;


    public PedidoStatusHistoricoLeitura(
            UUID pedidoUuid,
            String status,
            Instant dataStatus,
            String observacao
    ) {
        this.pedidoUuid = pedidoUuid;
        this.status = status;
        this.dataStatus = dataStatus;
        this.observacao = observacao;
    }

}