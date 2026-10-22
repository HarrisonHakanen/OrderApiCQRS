package com.example.OrderApiUpdate.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "pedido_itens_leitura")
@Getter
@Setter
@NoArgsConstructor
public class PedidoItemLeitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pedido_uuid", nullable = false)
    private UUID pedidoUuid;

    @Column(name = "produto_uuid", nullable = false)
    private UUID produtoUuid;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 19, scale = 2)
    private BigDecimal precoUnitario;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal desconto;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotal;

    public PedidoItemLeitura(UUID pedidoUuid, UUID produtoUuid, Integer quantidade, BigDecimal precoUnitario, BigDecimal desconto, BigDecimal subtotal) {
        this.pedidoUuid = pedidoUuid;
        this.produtoUuid = produtoUuid;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.desconto = desconto;
        this.subtotal = subtotal;
    }
}
