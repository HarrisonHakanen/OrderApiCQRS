package com.example.OrderApiInsert.service;

import com.example.OrderApiInsert.dto.AlterarStatusRequest;
import com.example.OrderApiInsert.dto.CancelarPedidoRequest;
import com.example.OrderApiInsert.dto.CriarPedidoRequest;

import java.util.UUID;

public interface PedidoCommandService {

    UUID solicitarCriacao(CriarPedidoRequest request);

    void solicitarAlteracaoStatus(
            UUID orderUUID,
            AlterarStatusRequest request);

    void solicitarCancelamento(
            UUID orderUUID,
            CancelarPedidoRequest request);
}
