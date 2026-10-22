package com.example.OrderApiRead.exeption;

import java.util.UUID;

public class PedidoNaoEncontradoException
        extends RuntimeException {

    public PedidoNaoEncontradoException(UUID pedidoUuid) {
        super("Pedido não encontrado: " + pedidoUuid);
    }
}
