package com.example.OrderApiProcess.service;

import com.example.OrderApiProcess.model.entity.Pedido;
import com.example.OrderApiProcess.model.entity.PedidoItem;

import java.util.List;

public interface OutboxService {

    void registrarPedidoCriado(Pedido pedido, List<PedidoItem> itens);
    void cancelarPedido(Pedido pedido);
    void alterarPedido(Pedido pedido);
}
