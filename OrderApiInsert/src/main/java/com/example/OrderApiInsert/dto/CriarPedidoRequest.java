package com.example.OrderApiInsert.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;


public record CriarPedidoRequest(
        @NotNull
        UUID clienteUuid,

        @NotEmpty
        List<@Valid ItemPedidoRequest> itens
) {

    public record ItemPedidoRequest(
            @NotNull
            UUID produtoUuid,

            @NotNull
            @Positive
            Integer quantidade
    ) {}
}
