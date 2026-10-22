package com.example.OrderApiUpdate.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pedidos_leitura")
@Getter
@Setter
@NoArgsConstructor
public class PedidoLeitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "pedido_uuid", nullable = false, unique = true)
    private UUID pedidoUuid;

    @Column(name = "cliente_uuid", nullable = false)
    private UUID clienteUuid;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal desconto;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal total;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    public PedidoLeitura(UUID pedidoUuid, UUID clienteUuid, String status, BigDecimal subtotal, BigDecimal desconto, BigDecimal total, Instant instant) {
        this.pedidoUuid = pedidoUuid;
        this.clienteUuid = clienteUuid;
        this.status = status;
        this.subtotal = subtotal;
        this.desconto = desconto;
        this.total = total;
        this.criadoEm = instant;
        this.atualizadoEm = Instant.now();
    }
}
